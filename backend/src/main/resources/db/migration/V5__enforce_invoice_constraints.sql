-- Invoice creator is now mandatory

ALTER TABLE invoices
    ALTER COLUMN created_by_user_id SET NOT NULL;


-- Invoice number must be unique according to the entity

ALTER TABLE invoices
    ADD CONSTRAINT uk_invoices_invoice_number
        UNIQUE (invoice_number);