import requests
import logging

logger = logging.getLogger(__name__)


def test_should_successfully_retrieve_products_when_valid_brand_is_provided(base_url):
    """Test that the API returns a list of products when a valid brand (e.g., GMX) is provided."""
    params = {"brand": "GMX"}
    response = requests.get(base_url, params=params)
    assert response.status_code == 200, f"Expected 200, got {response.status_code}"

    products = response.json()
    assert isinstance(products, list), "Expected a list of products"
    for product in products:
        assert "id" in product and "name" in product and "brand" in product, "Missing product fields"
    logger.info("Test Success: Products retrieved correctly for valid brand.")


def test_should_return_400_when_invalid_brand_format_is_provided(base_url):
    """Verify that the API returns 400 Bad Request when an invalid brand string is provided."""
    params = {"brand": "INVALID_BRAND"}
    response = requests.get(base_url, params=params)
    assert response.status_code == 400, f"Expected 400, got {response.status_code}"
    logger.info("Test Success: Invalid brand format handled correctly.")


def test_should_return_200_when_valid_brand_has_no_products(base_url):
    """Verify that the API returns 200 and an empty list when a valid brand from the enumeration has no products."""
    params = {"brand": "MAIL_COM"}
    response = requests.get(base_url, params=params)

    assert response.status_code == 200, f"Expected 200 for valid enum brand, got {response.status_code}"
    logger.info("Test Success: Valid brand with no products handled correctly.")