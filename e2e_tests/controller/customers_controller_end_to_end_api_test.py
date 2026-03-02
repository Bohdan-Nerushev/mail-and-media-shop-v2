import requests


def test_register_customer_success(valid_customer_payload, base_url, header):
    response = requests.post(base_url, headers=header, json=valid_customer_payload)
    assert response.status_code == 201, f"Expected 201, got {response.status_code}"
    data = response.json()
    assert data.get("status") == "INACTIVE", "Customer status should be INACTIVE"
    print("Customer registration success passed")
    return data["id"]


def test_register_customer_missing_firstname(base_url, header, missing_firstname_payload):
    response = requests.post(base_url, headers=header, json=missing_firstname_payload)
    assert response.status_code == 400, f"Expected 400, got {response.status_code}"
    print("Customer registration missing firstname passed")


def test_register_customer_invalid_email(base_url, header, invalid_email_payload):
    response = requests.post(base_url, headers=header, json=invalid_email_payload)
    assert response.status_code == 400, f"Expected 400, got {response.status_code}"
    print("Customer registration invalid email passed")


def test_get_customer_success(customer_id, base_url):
    url = f"{base_url}/{customer_id}"
    response = requests.get(url)
    assert response.status_code == 200, f"Expected 200, got {response.status_code}"
    print("Get customer success passed")


def test_get_customer_not_found(base_url, invalid_customer_id):
    url = f"{base_url}/{invalid_customer_id}"
    response = requests.get(url)
    assert response.status_code == 404, f"Expected 404, got {response.status_code}"
    print("Get customer not found passed")


def test_update_address_success(customer_id, base_url, header):
    url = f"{base_url}/{customer_id}/address"
    response = requests.put(url, headers=header, json={
        "street": "Neustraße",
        "number": "5",
        "postcode": "10115",
        "city": "Berlin",
        "country": "Germany"
    })
    assert response.status_code == 204, f"Expected 204, got {response.status_code}"
    print("Update address success passed")


def test_update_address_invalid(customer_id, base_url, header, invalid_address_payload):
    url = f"{base_url}/{customer_id}/address"
    response = requests.put(url, headers=header, json=invalid_address_payload["address"])
    assert response.status_code == 400, f"Expected 400, got {response.status_code}"
    print("Update address invalid data passed")


def test_deactivate_customer(customer_id, base_url):
    url_deactivate = f"{base_url}/{customer_id}/deactivate"
    response = requests.put(url_deactivate)
    assert response.status_code == 204, f"Expected 204, got {response.status_code}"
    print("Customer deactivation passed")

def test_activate_customer(customer_id, base_url):
    url_activate = f"{base_url}/{customer_id}/activate"
    response = requests.put(url_activate)
    assert response.status_code == 204, f"Expected 204, got {response.status_code}"
    print("Customer activation passed")


def test_update_communication_details(customer_id, base_url, header):
    url = f"{base_url}/{customer_id}/communication-details"
    payload = {"email": "new.email@gmx.de", "telephone": "+49 621 654321"}
    response = requests.put(url, headers=header, json=payload)
    assert response.status_code == 204, f"Expected 204, got {response.status_code}"
    print("Update communication details passed")

def get_valid_product_id(brand="GMX"):
    url = f"http://localhost:8080/api/v1/products?brand={brand}"
    response = requests.get(url)
    assert response.status_code == 200, "Should be able to fetch products"
    products = response.json()
    assert len(products) > 0, f"No products found for brand {brand}"
    return products[0]["id"]


def test_purchase_product_success(customer_id, product_id, base_url, header):
    url = f"{base_url}/{customer_id}/purchases"
    payload = {"productId": product_id}
    response = requests.post(url, headers=header, json=payload)
    assert response.status_code == 201, f"Expected 201, got {response.status_code}"
    print("Purchase product success passed")


def test_delete_customer(customer_id,base_url):
    url = f"{base_url}/{customer_id}"
    response = requests.delete(url)
    assert response.status_code == 204, f"Expected 204, got {response.status_code}"
    print("Delete customer passed")