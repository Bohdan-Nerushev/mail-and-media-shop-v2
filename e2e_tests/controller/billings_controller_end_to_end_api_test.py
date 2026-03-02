import requests


def test_generate_invoice_success(valid_customer_id, base_url, header):
    url = f"{base_url}/{valid_customer_id}/invoice"
    response = requests.post(url, headers=header)

    assert response.status_code == 200, f"Expected 200, got {response.status_code}"
    data = response.json()

    required_fields = ["customerId", "invoiceDate", "totalAmount", "items"]
    for field in required_fields:
        assert field in data, f"Missing field: {field}"

    assert isinstance(data["items"], list), "Items should be a list"
    print("Test Success: Invoice generated correctly.")

def test_generate_invoice_customer_not_found(base_url, invalid_customer_id, header,  customer_id=None):
    url = f"{base_url}/{invalid_customer_id}/invoice"
    response = requests.post(url, headers=header)

    assert response.status_code == 404, f"Expected 404, got {response.status_code}"
    print("Test Success: Correctly handled customer not found.")

def test_generate_invoice_server_error(base_url, header, error_id):
    url = f"{base_url}/{error_id}/invoice"
    response = requests.post(url, headers=header)

    assert response.status_code == 500, f"Expected 500, got {response.status_code}"
    print("Test Success: Correctly handled server error.")

def test_generate_invoice_idempotency(customer_id, base_url, header):
    url = f"{base_url}/{customer_id}/invoice"
    res1 = requests.post(url, headers=header)
    assert res1.status_code == 200
    
    res2 = requests.post(url, headers=header)
    assert res2.status_code == 200
    print("Test Success: Invoice generation idempotency check passed.")

def test_generate_invoice_inactive_customer(customer_id, base_url, header):
    url = f"{base_url}/{customer_id}/invoice"
    response = requests.post(url, headers=header)
    assert response.status_code == 409, f"Expected 409 for inactive customer invoice generation, got {response.status_code}"
    print("Test Success: Invoice generation for inactive customer handled (409) passed.")