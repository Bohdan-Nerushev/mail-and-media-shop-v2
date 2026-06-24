-- Migration to update foreign key constraints from RESTRICT to CASCADE
-- This allows deleting a customer and automatically removing all their contracts and invoices.

ALTER TABLE contracts
    DROP CONSTRAINT IF EXISTS contracts_customer_id_fkey;

ALTER TABLE contracts
    ADD CONSTRAINT contracts_customer_id_fkey
    FOREIGN KEY (customer_id)
    REFERENCES customers(customer_id)
    ON DELETE CASCADE;

ALTER TABLE invoices
    DROP CONSTRAINT IF EXISTS invoices_customer_id_fkey;

ALTER TABLE invoices
    ADD CONSTRAINT invoices_customer_id_fkey
    FOREIGN KEY (customer_id)
    REFERENCES customers(customer_id)
    ON DELETE CASCADE;

ALTER TABLE invoice_items
    DROP CONSTRAINT IF EXISTS invoice_items_contract_id_fkey;

ALTER TABLE invoice_items
    ADD CONSTRAINT invoice_items_contract_id_fkey
    FOREIGN KEY (contract_id)
    REFERENCES contracts(id)
    ON DELETE CASCADE;
