import requests


def test_get_products_success(base_url):
    params = {"brand": "GMX"}
    response = requests.get(base_url, params=params)
    assert response.status_code == 200, f"Expected 200, got {response.status_code}"

    products = response.json()
    assert isinstance(products, list), "Expected a list of products"
    for product in products:
        assert "id" in product and "name" in product and "brand" in product, "Missing product fields"
    print("Test Success: Products retrieved correctly.")


def test_get_products_invalid_brand(base_url):
    params = {"brand": "INVALID_BRAND"}
    response = requests.get(base_url, params=params)
    assert response.status_code == 400, f"Expected 400, got {response.status_code}"
    print("Test Success: Invalid brand handled correctly.")


def test_get_products_brand_not_found(base_url):
    params = {"brand": "MAIL_COM"}
    response = requests.get(base_url, params=params)

    assert response.status_code == 200, f"Expected 200 for valid enum brand, got {response.status_code}"
    print("Test Success: Valid brand handled correctly.")