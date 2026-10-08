-- ============================================================
-- 1. Migrate old Client -> User relationships
--    clients.user_id -> user_client
-- ============================================================

INSERT INTO user_client (user_id, client_id)
SELECT c.user_id, c.id
FROM clients c
WHERE c.user_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM user_client uc
    WHERE uc.user_id = c.user_id
      AND uc.client_id = c.id
);


-- ============================================================
-- 2. Add created_by_user_id to invoices
-- ============================================================

ALTER TABLE invoices
    ADD COLUMN created_by_user_id BIGINT;


-- ============================================================
-- 3. Migrate old invoice -> user relationship
--    invoices.user_id -> invoices.created_by_user_id
-- ============================================================

UPDATE invoices
SET created_by_user_id = user_id
WHERE user_id IS NOT NULL;


-- ============================================================
-- 4. Add foreign key for invoice creator
-- ============================================================

ALTER TABLE invoices
    ADD CONSTRAINT fk_invoice_created_by_user
        FOREIGN KEY (created_by_user_id)
            REFERENCES users(id);


-- ============================================================
-- 5. Convert PaymentAmountStatus from ordinal to string
--
-- 0 = PARTIAL
-- 1 = FULL
-- 2 = DORMANT
-- ============================================================

ALTER TABLE invoices
ALTER COLUMN amount_status TYPE VARCHAR(50)
USING CASE amount_status
    WHEN 0 THEN 'PARTIAL'
    WHEN 1 THEN 'FULL'
    WHEN 2 THEN 'DORMANT'
    ELSE NULL
END;


-- ============================================================
-- 6. Convert PaymentStatus from ordinal to string
--
-- 0 = PENDING
-- 1 = SUCCESSFUL
-- 2 = FAILED
-- ============================================================

ALTER TABLE payment
ALTER COLUMN status TYPE VARCHAR(50)
USING CASE status
    WHEN 0 THEN 'PENDING'
    WHEN 1 THEN 'SUCCESSFUL'
    WHEN 2 THEN 'FAILED'
    ELSE NULL
END;