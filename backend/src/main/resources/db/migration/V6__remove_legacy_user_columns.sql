ALTER TABLE users
DROP COLUMN IF EXISTS is_email_verified;

ALTER TABLE users
DROP COLUMN IF EXISTS created_at_ts;

ALTER TABLE users
DROP COLUMN IF EXISTS updated_at_ts;