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