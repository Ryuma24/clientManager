-- ============================================================
-- Remove old Client -> User relationship
-- ============================================================

ALTER TABLE clients
DROP CONSTRAINT IF EXISTS fktiuqdledq2lybrds2k3rfqrv4;

ALTER TABLE clients
DROP COLUMN IF EXISTS user_id;


-- ============================================================
-- Remove old Invoice -> User relationship
-- ============================================================

ALTER TABLE invoices
DROP CONSTRAINT IF EXISTS fk_invoice_user;

ALTER TABLE invoices
DROP COLUMN IF EXISTS user_id;