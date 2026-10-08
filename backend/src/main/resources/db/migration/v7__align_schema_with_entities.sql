-- Migrate legacy client ownership and invoice creator relationships.

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

-- Link already-registered client accounts to matching profiles.
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

-- Convert enum ordinals to names, and tolerate databases where values were
-- already converted to strings before this migration ran.
DO $$
BEGIN
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

DO $$
BEGIN
	IF EXISTS (
		SELECT 1 FROM public.payment
		WHERE status IS NOT NULL
		  AND upper(trim(status::text)) NOT IN
			  ('0', '1', '2', 'PENDING', 'SUCCESSFUL', 'FAILED')
	) THEN
		RAISE EXCEPTION 'payment.status contains values that cannot be migrated';
END IF;
END
$$;

ALTER TABLE public.payment
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
