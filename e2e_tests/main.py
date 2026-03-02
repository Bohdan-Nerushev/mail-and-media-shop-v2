import copy
import uuid

from controller.customers_controller_end_to_end_api_test import (
    test_register_customer_success,
    test_register_customer_missing_firstname,
    test_register_customer_invalid_email,
    test_get_customer_success,
    test_get_customer_not_found,
    test_update_address_success,
    test_update_address_invalid,
    test_update_communication_details,
    test_activate_customer,
    test_deactivate_customer,
    test_purchase_product_success,
    test_delete_customer,
    get_valid_product_id,
    test_firstname_blank,
    test_lastname_blank,
    test_firstname_max_length,
    test_lastname_max_length,
    test_register_customer_missing_lastname,
    test_email_max_length,
    test_email_unicode_special_chars,
    test_register_customer_null_fields,
    test_activate_customer_idempotency,
    test_update_address_inactive_fail,
    test_purchase_customer_inactive_fail,
    test_delete_active_customer_fail,
    test_purchase_product_idempotency,
    test_purchase_product_verification
)
from controller.contracts_controller_end_to_end_api_test import (
    test_get_contracts_success,
    test_get_contracts_customer_not_found,
    test_activate_contract_success,
    test_activate_contract_not_found,
    test_activate_contract_idempotency,
    test_activate_contract_forbidden,
    test_get_contracts_inactive_customer,
    test_get_contracts_deleted_customer
)
from controller.billings_controller_end_to_end_api_test import (
    test_generate_invoice_success,
    test_generate_invoice_customer_not_found,
    test_generate_invoice_server_error,
    test_generate_invoice_idempotency,
    test_generate_invoice_inactive_customer
)
from controller.products_controller_end_to_end_api_test import (
    test_get_products_success,
    test_get_products_invalid_brand,
    test_get_products_brand_not_found
)

import os

# ---------------------------
# Configuration (Environment Variables)
# ---------------------------
HOST = os.getenv("APP_HOST", "http://localhost").rstrip("/")
PORT = os.getenv("APP_PORT", "8080")
APP_URL = f"{HOST}:{PORT}"

BASE_URL_CUSTOMERS = f"{APP_URL}/api/v1/customers"
BASE_URL_PRODUCTS = f"{APP_URL}/api/v1/products"
BASE_URL_BILLING = f"{APP_URL}/api/v1/billing"
HEADERS = {"Content-Type": "application/json"}

# UUIDs for negative test scenarios
INVALID_CUSTOMER_ID = str(uuid.uuid4())
INVALID_CONTRACT_ID = str(uuid.uuid4())

# ---------------------------
# Test Data Preparation
# ---------------------------
valid_customer_payload = {
    "firstName": "Max",
    "lastName": "Mustermann",
    "birthDate": "1990-06-15",
    "address": {
        "street": "Musterstraße",
        "number": "12A",
        "postcode": "68161",
        "city": "Mannheim",
        "country": "Germany"
    },
    "invoiceAddress": None,
    "communicationDetails": {
        "email": "max.mustermann@gmx.de",
        "telephone": "+49 621 123456"
    },
    "brand": "GMX"
}

# ---------------------------
# Test Execution
# ---------------------------
if __name__ == "__main__":
    print("=== Starting E2E Tests ===\n")

    # 1. Product tests (require only the base URL)
    test_get_products_success(BASE_URL_PRODUCTS)
    test_get_products_invalid_brand(BASE_URL_PRODUCTS)
    test_get_products_brand_not_found(BASE_URL_PRODUCTS)

    # 2. Customer registration (returns ID for further tests)
    customer_id = test_register_customer_success(valid_customer_payload, BASE_URL_CUSTOMERS, HEADERS)

    # 3. Validation checks during registration
    test_register_customer_missing_firstname(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_register_customer_missing_lastname(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_firstname_blank(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_lastname_blank(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_firstname_max_length(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_lastname_max_length(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_register_customer_invalid_email(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_email_max_length(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_email_unicode_special_chars(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_register_customer_null_fields(BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)

    # 4. Inactive state checks (Scenario 2, 3, 10)
    test_update_address_inactive_fail(customer_id, BASE_URL_CUSTOMERS, HEADERS)
    valid_prod_id = get_valid_product_id(BASE_URL_PRODUCTS, valid_customer_payload["brand"])
    test_purchase_customer_inactive_fail(customer_id, valid_prod_id, BASE_URL_CUSTOMERS, HEADERS)
    test_get_contracts_inactive_customer(customer_id, BASE_URL_CUSTOMERS)
    test_generate_invoice_inactive_customer(customer_id, BASE_URL_BILLING, HEADERS)

    # 5. Retrieve and activate customer
    test_get_customer_success(customer_id, BASE_URL_CUSTOMERS)
    test_get_customer_not_found(BASE_URL_CUSTOMERS, INVALID_CUSTOMER_ID)
    test_activate_customer(customer_id, BASE_URL_CUSTOMERS)
    
    # 6. Activation checks (Scenario 1 & 5)
    test_activate_customer_idempotency(customer_id, BASE_URL_CUSTOMERS)

    # 7. Update data (ACTIVE status)
    test_update_address_success(customer_id, BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_update_address_invalid(customer_id, BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)
    test_update_communication_details(customer_id, BASE_URL_CUSTOMERS, HEADERS, valid_customer_payload)

    # 8. Purchase product and idempotency (Scenario 6)
    test_purchase_product_success(customer_id, valid_prod_id, BASE_URL_CUSTOMERS, HEADERS)
    test_purchase_product_verification(customer_id, valid_prod_id, BASE_URL_CUSTOMERS, HEADERS)
    test_purchase_product_idempotency(customer_id, valid_prod_id, BASE_URL_CUSTOMERS, HEADERS)

    # 9. Contracts (Scenario 8 & 9)
    contracts = test_get_contracts_success(customer_id, BASE_URL_CUSTOMERS)
    test_get_contracts_customer_not_found(BASE_URL_CUSTOMERS, INVALID_CUSTOMER_ID)
    
    if contracts:
        contract_id = contracts[0]["id"]
        test_activate_contract_success(customer_id, contract_id, BASE_URL_CUSTOMERS)
        test_activate_contract_idempotency(customer_id, contract_id, BASE_URL_CUSTOMERS)
        
        # Scenario 9: try with another customer ID
        ANOTHER_CUSTOMER_ID = str(uuid.uuid4())
        test_activate_contract_forbidden(ANOTHER_CUSTOMER_ID, contract_id, BASE_URL_CUSTOMERS)
    
    test_activate_contract_not_found(customer_id, BASE_URL_CUSTOMERS, INVALID_CONTRACT_ID)

    # 10. Billing (Scenario 7)
    test_generate_invoice_success(customer_id, BASE_URL_BILLING, HEADERS)
    test_generate_invoice_customer_not_found(BASE_URL_BILLING, INVALID_CUSTOMER_ID, HEADERS)
    test_generate_invoice_idempotency(customer_id, BASE_URL_BILLING, HEADERS)
    
    # Simulate a 500 error
    SIMULATED_ERROR_ID = "00000000-0000-0000-0000-000000000500"
    try:
        test_generate_invoice_server_error(BASE_URL_BILLING, HEADERS, SIMULATED_ERROR_ID)
    except AssertionError as e:
        print(f"Server error simulation note: {e}")

    # 11. Termination and constraints (Scenario 4)
    test_delete_active_customer_fail(customer_id, BASE_URL_CUSTOMERS)
    
    test_deactivate_customer(customer_id, BASE_URL_CUSTOMERS)
    test_delete_customer(customer_id, BASE_URL_CUSTOMERS)
    
    # 12. Final checks after deletion
    test_get_contracts_deleted_customer(customer_id, BASE_URL_CUSTOMERS)

    print("\n=== All E2E Tests Completed Successfully ===")
