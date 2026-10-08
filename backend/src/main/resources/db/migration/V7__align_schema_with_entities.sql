-- Reconcile deployed legacy schema with the current entity mappings.

CREATE TABLE IF NOT EXISTS public.user_client (
    user_id BIGINT NOT NULL REFERENCES public.users(id),
    client_id BIGINT NOT NULL REFERENCES public.clients(id),
    PRIMARY KEY (user_id, client_id)
);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'clients'
          AND column_name = 'user_id'
    ) THEN
        EXECUTE '
            INSERT INTO public.user_client (user_id, client_id)
            SELECT user_id, id
            FROM public.clients
            WHERE user_id IS NOT NULL
            ON CONFLICT DO NOTHING
        ';
    END IF;
END
$$;

INSERT INTO public.user_client (user_id, client_id)
SELECT u.id, c.id
FROM public.users u
JOIN public.clients c
  ON lower(trim(u.email)) = lower(trim(c.email))
WHERE upper(trim(u.role::text)) = 'CLIENT'
ON CONFLICT DO NOTHING;

ALTER TABLE public.invoices
    ADD COLUMN IF NOT EXISTS created_by_user_id BIGINT;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'invoices'
          AND column_name = 'user_id'
    ) THEN
        EXECUTE '
            UPDATE public.invoices
            SET created_by_user_id = user_id
            WHERE created_by_user_id IS NULL
              AND user_id IS NOT NULL
        ';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_invoice_created_by_user'
          AND conrelid = 'public.invoices'::regclass
    ) THEN
        ALTER TABLE public.invoices
            ADD CONSTRAINT fk_invoice_created_by_user
            FOREIGN KEY (created_by_user_id) REFERENCES public.users(id);
    END IF;
END
$$;

DO $$
DECLARE constraint_name text;
BEGIN
    -- Legacy ordinal checks (for example, amount_status >= 0) cannot be
    -- re-evaluated after converting the enum column to VARCHAR.
    FOR constraint_name IN
        SELECT DISTINCT c.conname
        FROM pg_constraint c
        JOIN pg_attribute a
          ON a.attrelid = c.conrelid
         AND a.attnum = ANY(c.conkey)
        WHERE c.conrelid = 'public.invoices'::regclass
          AND c.contype = 'c'
          AND a.attname = 'amount_status'
    LOOP
        EXECUTE format('ALTER TABLE public.invoices DROP CONSTRAINT %I', constraint_name);
    END LOOP;

    IF EXISTS (
        SELECT 1 FROM public.invoices
        WHERE amount_status IS NOT NULL
          AND upper(trim(amount_status::text)) NOT IN
              ('0', '1', '2', 'PARTIAL', 'FULL', 'DORMANT')
    ) THEN
        RAISE EXCEPTION 'invoices.amount_status contains values that cannot be migrated';
    END IF;
END
$$;

ALTER TABLE public.invoices
    ALTER COLUMN amount_status TYPE VARCHAR(50)
    USING CASE upper(trim(amount_status::text))
        WHEN '0' THEN 'PARTIAL'
        WHEN '1' THEN 'FULL'
        WHEN '2' THEN 'DORMANT'
        WHEN 'PARTIAL' THEN 'PARTIAL'
        WHEN 'FULL' THEN 'FULL'
        WHEN 'DORMANT' THEN 'DORMANT'
        ELSE NULL
    END;

ALTER TABLE public.invoices
    ADD CONSTRAINT invoices_amount_status_check
    CHECK (amount_status IS NULL OR amount_status IN ('PARTIAL', 'FULL', 'DORMANT'));

DO $$
DECLARE constraint_name text;
BEGIN
    FOR constraint_name IN
        SELECT DISTINCT c.conname
        FROM pg_constraint c
        JOIN pg_attribute a
          ON a.attrelid = c.conrelid
         AND a.attnum = ANY(c.conkey)
        WHERE c.conrelid = 'public.payments'::regclass
          AND c.contype = 'c'
          AND a.attname = 'status'
    LOOP
        EXECUTE format('ALTER TABLE public.payments DROP CONSTRAINT %I', constraint_name);
    END LOOP;

    IF EXISTS (
        SELECT 1 FROM public.payments
        WHERE status IS NOT NULL
          AND upper(trim(status::text)) NOT IN
              ('0', '1', '2', 'PENDING', 'SUCCESSFUL', 'FAILED')
    ) THEN
        RAISE EXCEPTION 'payments.status contains values that cannot be migrated';
    END IF;
END
$$;

ALTER TABLE public.payments
    ALTER COLUMN status TYPE VARCHAR(50)
    USING CASE upper(trim(status::text))
        WHEN '0' THEN 'PENDING'
        WHEN '1' THEN 'SUCCESSFUL'
        WHEN '2' THEN 'FAILED'
        WHEN 'PENDING' THEN 'PENDING'
        WHEN 'SUCCESSFUL' THEN 'SUCCESSFUL'
        WHEN 'FAILED' THEN 'FAILED'
        ELSE NULL
    END;

ALTER TABLE public.payments
    ADD CONSTRAINT payments_status_check
    CHECK (status IS NULL OR status IN ('PENDING', 'SUCCESSFUL', 'FAILED'));
