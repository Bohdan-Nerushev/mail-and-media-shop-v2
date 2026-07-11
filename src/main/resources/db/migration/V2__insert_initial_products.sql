-- Insert initial products from CSV data
DO
$$
DECLARE
    -- GMX Products
gmx_basic_id UUID := gen_random_uuid();
    gmx_premium_id
UUID := gen_random_uuid();
    gmx_storage_id
UUID := gen_random_uuid();
    gmx_music_id
UUID := gen_random_uuid();
    gmx_security_id
UUID := gen_random_uuid();

    -- WEB_DE Products
    webde_basic_id
UUID := gen_random_uuid();
    webde_premium_id
UUID := gen_random_uuid();
    webde_storage_id
UUID := gen_random_uuid();
    webde_music_id
UUID := gen_random_uuid();
    webde_security_id
UUID := gen_random_uuid();

    -- MAIL_COM Products
    mailcom_basic_id
UUID := gen_random_uuid();
    mailcom_premium_id
UUID := gen_random_uuid();
    mailcom_storage_id
UUID := gen_random_uuid();
    mailcom_music_id
UUID := gen_random_uuid();
    mailcom_security_id
UUID := gen_random_uuid();
BEGIN
    -- GMX Basic Mail
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (gmx_basic_id, 'GMX Basic Mail', 'GMX', 0.00, 0.50, 'StandardMailProduct');
INSERT INTO mail_products (id, storage_size)
VALUES (gmx_basic_id, 4);
INSERT INTO standard_mail_products (id)
VALUES (gmx_basic_id);

-- GMX Premium Mail
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (gmx_premium_id, 'GMX Premium Mail', 'GMX', 0.00, 4.99, 'PremiumMailProduct');
INSERT INTO mail_products (id, storage_size)
VALUES (gmx_premium_id, 8);
INSERT INTO premium_mail_products (id)
VALUES (gmx_premium_id);

-- GMX Partner Products
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (gmx_storage_id, 'GMX Storage Pro', 'GMX', 9.99, 1.99, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (gmx_storage_id);

INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (gmx_music_id, 'GMX Music Streaming', 'GMX', 0.00, 9.99, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (gmx_music_id);

INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (gmx_security_id, 'GMX Security Pack', 'GMX', 4.99, 2.49, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (gmx_security_id);

-- WEB_DE Basic Mail
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (webde_basic_id, 'WEB_DE Basic Mail', 'WEB_DE', 0.00, 0.50, 'StandardMailProduct');
INSERT INTO mail_products (id, storage_size)
VALUES (webde_basic_id, 4);
INSERT INTO standard_mail_products (id)
VALUES (webde_basic_id);

-- WEB_DE Premium Mail
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (webde_premium_id, 'WEB_DE Premium Mail', 'WEB_DE', 0.00, 4.99, 'PremiumMailProduct');
INSERT INTO mail_products (id, storage_size)
VALUES (webde_premium_id, 8);
INSERT INTO premium_mail_products (id)
VALUES (webde_premium_id);

-- WEB_DE Partner Products
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (webde_storage_id, 'WEB_DE Storage Pro', 'WEB_DE', 9.99, 1.99, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (webde_storage_id);

INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (webde_music_id, 'WEB_DE Music Streaming', 'WEB_DE', 0.00, 9.99, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (webde_music_id);

INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (webde_security_id, 'WEB_DE Security Pack', 'WEB_DE', 4.99, 2.49, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (webde_security_id);

-- MAIL_COM Basic Mail
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (mailcom_basic_id, 'MAIL_COM Basic Mail', 'MAIL_COM', 0.00, 0.50, 'StandardMailProduct');
INSERT INTO mail_products (id, storage_size)
VALUES (mailcom_basic_id, 4);
INSERT INTO standard_mail_products (id)
VALUES (mailcom_basic_id);

-- MAIL_COM Premium Mail
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (mailcom_premium_id, 'MAIL_COM Premium Mail', 'MAIL_COM', 0.00, 4.99, 'PremiumMailProduct');
INSERT INTO mail_products (id, storage_size)
VALUES (mailcom_premium_id, 8);
INSERT INTO premium_mail_products (id)
VALUES (mailcom_premium_id);

-- MAIL_COM Partner Products
INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (mailcom_storage_id, 'MAIL_COM Storage Pro', 'MAIL_COM', 9.99, 1.99, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (mailcom_storage_id);

INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (mailcom_music_id, 'MAIL_COM Music Streaming', 'MAIL_COM', 0.00, 9.99, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (mailcom_music_id);

INSERT INTO products (id, name, brand, setup_fee, monthly_fee, dtype)
VALUES (mailcom_security_id, 'MAIL_COM Security Pack', 'MAIL_COM', 4.99, 2.49, 'PartnerProduct');
INSERT INTO partner_products (id)
VALUES (mailcom_security_id);

END $$;
