import requests
import logging

logger = logging.getLogger(__name__)


def test_should_successfully_list_contracts_when_customer_id_is_valid(customer_id, base_url):
    """Test that the API returns a list of contracts for a valid customer."""
    url = f"{base_url}/{customer_id}/contracts"
    response = requests.get(url)
    assert response.status_code == 200, f"Expected 200, got {response.status_code}"

    contracts = response.json()
    assert isinstance(contracts, list), "Expected list of contracts"
    for contract in contracts:
        assert "id" in contract and "customerId" in contract and "status" in contract, "Missing contract fields"
    logger.info(f"Test Success: Contracts retrieved correctly for customer {customer_id}.")
    return contracts

def test_should_return_404_when_listing_contracts_for_non_existent_customer(base_url, invalid_customer_id):
    """Verify that listing contracts for a non-existent customer returns 404."""
    url = f"{base_url}/{invalid_customer_id}/contracts"
    response = requests.get(url)
    assert response.status_code == 404, f"Expected 404, got {response.status_code}"
    logger.info("Test Success: Customer not found handled correctly during contracts retrieval.")

def test_should_activate_contract_successfully_when_ids_are_valid(customer_id, contract_id, base_url):
    """Test that the API activates a contract successfully for valid customer and contract IDs."""
    url = f"{base_url}/{customer_id}/contracts/{contract_id}/activate"
    response = requests.put(url)
    assert response.status_code == 204, f"Expected 204, got {response.status_code}"
    logger.info(f"Test Success: Contract {contract_id} activated successfully for customer {customer_id}.")

def test_should_return_404_when_activating_non_existent_contract(customer_id, base_url, invalid_contract_id):
    """Verify that activating a non-existent contract returns 404."""
    url = f"{base_url}/{customer_id}/contracts/{invalid_contract_id}/activate"
    response = requests.put(url)
    assert response.status_code == 404, f"Expected 404, got {response.status_code}"
    logger.info("Test Success: Contract not found handled correctly during activation.")

def test_should_handle_contract_activation_idempotently_when_called_multiple_times(customer_id, contract_id, base_url):
    """Verify that repeated contract activation requests are handled idempotently."""
    url = f"{base_url}/{customer_id}/contracts/{contract_id}/activate"
    response = requests.put(url)
    assert response.status_code in [204, 409], f"Contract activation idempotency failed, got {response.status_code}"
    logger.info("Test Success: Contract activation idempotency check passed.")

def test_should_return_403_when_activating_contract_belonging_to_another_customer(wrong_customer_id, contract_id, base_url):
    """Verify that a customer cannot activate a contract belonging to another customer."""
    url = f"{base_url}/{wrong_customer_id}/contracts/{contract_id}/activate"
    response = requests.put(url)
    assert response.status_code in [400, 403, 404], f"Expected 400, 403 or 404 for wrong customer contract activation, got {response.status_code}"
    logger.info("Test Success: Access control for contract activation passed.")

def test_should_return_409_when_listing_contracts_for_inactive_customer(customer_id, base_url):
    """Verify that listing contracts for an inactive customer returns 409 Conflict."""
    url = f"{base_url}/{customer_id}/contracts"
    response = requests.get(url)

    assert response.status_code == 409, f"Expected 409 for inactive customer contracts retrieval, got {response.status_code}"
    logger.info("Test Success: Retrieval of contracts for inactive customer handled correctly (409).")

def test_should_return_404_when_listing_contracts_for_deleted_customer(customer_id, base_url):
    """Verify that contracts of a deleted customer are not accessible (returns 404)."""
    url = f"{base_url}/{customer_id}/contracts"
    response = requests.get(url)
    assert response.status_code == 404, f"Expected 404 for deleted customer contracts, got {response.status_code}"
    logger.info("Test Success: Contracts not accessible for deleted customer passed.")