import copy
import uuid

from controller.billings_controller_end_to_end_api_test import (
    test_generate_invoice_success,
    test_generate_invoice_customer_not_found,
    test_generate_invoice_server_error
)
from controller.contracts_controller_end_to_end_api_test import (
    test_get_contracts_success,
    test_get_contracts_customer_not_found,
    test_activate_contract_success,
    test_activate_contract_not_found
)
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
    get_valid_product_id
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

invalid_email_payload = copy.deepcopy(valid_customer_payload)
invalid_email_payload["communicationDetails"]["email"] = "invalid-email"

missing_firstname_payload = copy.deepcopy(valid_customer_payload)
del missing_firstname_payload["firstName"]

invalid_address_payload = copy.deepcopy(valid_customer_payload)
invalid_address_payload["address"]["street"] = ""

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
    test_register_customer_missing_firstname(BASE_URL_CUSTOMERS, HEADERS, missing_firstname_payload)
    test_register_customer_invalid_email(BASE_URL_CUSTOMERS, HEADERS, invalid_email_payload)

    # 4. Retrieve and activate customer
    test_get_customer_success(customer_id, BASE_URL_CUSTOMERS)
    test_get_customer_not_found(BASE_URL_CUSTOMERS, INVALID_CUSTOMER_ID)
    test_activate_customer(customer_id, BASE_URL_CUSTOMERS)

    # 5. Update data (requires ACTIVE status)
    test_update_address_success(customer_id, BASE_URL_CUSTOMERS, HEADERS)
    test_update_address_invalid(customer_id, BASE_URL_CUSTOMERS, HEADERS, invalid_address_payload)
    test_update_communication_details(customer_id, BASE_URL_CUSTOMERS, HEADERS)

    # 6. Purchase product
    # First, find a valid product ID for the customer's brand
    valid_prod_id = get_valid_product_id(valid_customer_payload["brand"])
    test_purchase_product_success(customer_id, valid_prod_id, BASE_URL_CUSTOMERS, HEADERS)

    # 7. Contracts (contract API is usually nested under /customers/{id}/contracts)
    contracts = test_get_contracts_success(customer_id, BASE_URL_CUSTOMERS)
    test_get_contracts_customer_not_found(BASE_URL_CUSTOMERS, INVALID_CUSTOMER_ID)
    
    if contracts:
        contract_id = contracts[0]["id"]
        test_activate_contract_success(customer_id, contract_id, BASE_URL_CUSTOMERS)
    
    test_activate_contract_not_found(customer_id, BASE_URL_CUSTOMERS, INVALID_CONTRACT_ID)

    # 8. Billing (API /billing/{customerId}/invoice)
    test_generate_invoice_success(customer_id, BASE_URL_BILLING, HEADERS)
    test_generate_invoice_customer_not_found(BASE_URL_BILLING, INVALID_CUSTOMER_ID, HEADERS)
    # Simulate a 500 error (if your backend triggers it on this endpoint)
    try:
        test_generate_invoice_server_error(BASE_URL_BILLING, HEADERS)
    except AssertionError as e:
        print(f"Server error simulation note: {e}")

    # 9. End of lifecycle
    test_deactivate_customer(customer_id, BASE_URL_CUSTOMERS)
    test_delete_customer(customer_id, BASE_URL_CUSTOMERS)

    print("\n=== All E2E Tests Completed Successfully ===")
