CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS addresses (
    id      SERIAL PRIMARY KEY,
    street  VARCHAR(250) NOT NULL,
    number  VARCHAR(100) NOT NULL,
    postcode VARCHAR(100) NOT NULL,
    city    VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS communication_details (
    id        SERIAL PRIMARY KEY,
    email     VARCHAR(255) NOT NULL,
    telephone VARCHAR(30)  NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name               VARCHAR(100) NOT NULL,
    last_name                VARCHAR(100) NOT NULL,
    birth_date               DATE         NOT NULL,
    address_id               INT          NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    invoice_address_id       INT          REFERENCES addresses(id) ON DELETE RESTRICT,
    communication_details_id INT          REFERENCES communication_details(id) ON DELETE RESTRICT,
    brand                    VARCHAR(50)  NOT NULL,
    status                   VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    brand       VARCHAR(50)  NOT NULL,
    setup_fee   NUMERIC(10, 2) NOT NULL CHECK (setup_fee >= 0.00),
    monthly_fee NUMERIC(10, 2) NOT NULL CHECK (monthly_fee >= 0.11),
    dtype       VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS mail_products (
    id           UUID   PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    storage_size BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS premium_mail_products (
    id UUID PRIMARY KEY REFERENCES mail_products(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS standard_mail_products (
    id UUID PRIMARY KEY REFERENCES mail_products(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS partner_products (
    id UUID PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bundle_products (
    id                 UUID PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    mail_product_id    UUID NOT NULL REFERENCES mail_products(id) ON DELETE RESTRICT,
    partner_product_id UUID NOT NULL REFERENCES partner_products(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS contracts (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id   UUID        NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    product_type  VARCHAR(50) NOT NULL,
    product_id    UUID        NOT NULL,
    creation_date DATE        NOT NULL,
    status        VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE IF NOT EXISTS invoices (
    id               UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    brand            VARCHAR(50)    NOT NULL,
    invoice_date     DATE           NOT NULL,
    customer_id      UUID           NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    address_id       INT            NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    invoice_address_id INT          NOT NULL REFERENCES addresses(id) ON DELETE RESTRICT,
    total_setup_fee  NUMERIC(10, 2) NOT NULL CHECK (total_setup_fee >= 0),
    total_monthly_fee NUMERIC(10, 2) NOT NULL CHECK (total_monthly_fee >= 0),
    discount         NUMERIC(10, 2) NOT NULL CHECK (discount >= 0),
    total_amount     NUMERIC(10, 2) NOT NULL CHECK (total_amount >= 0)
);

CREATE TABLE IF NOT EXISTS invoice_items (
    id                    UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id            UUID           NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    product_id            UUID           NOT NULL,
    contract_id           UUID           NOT NULL REFERENCES contracts(id) ON DELETE RESTRICT,
    product_name          VARCHAR(150)   NOT NULL,
    contract_creation_date DATE          NOT NULL,
    setup_fee             NUMERIC(10, 2) NOT NULL CHECK (setup_fee >= 0),
    monthly_fee           NUMERIC(10, 2) NOT NULL CHECK (monthly_fee >= 0)
);