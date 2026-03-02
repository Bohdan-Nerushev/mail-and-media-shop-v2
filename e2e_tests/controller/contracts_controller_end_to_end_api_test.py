import requests


def test_get_contracts_success(customer_id, base_url):
    url = f"{base_url}/{customer_id}/contracts"
    response = requests.get(url)
    assert response.status_code == 200, f"Expected 200, got {response.status_code}"

    contracts = response.json()
    assert isinstance(contracts, list), "Expected list of contracts"
    for contract in contracts:
        assert "id" in contract and "customerId" in contract and "status" in contract, "Missing contract fields"
    print("Test Success: Contracts retrieved correctly.")
    return contracts

def test_get_contracts_customer_not_found(base_url, invalid_customer_id):
    url = f"{base_url}/{invalid_customer_id}/contracts"
    response = requests.get(url)
    assert response.status_code == 404, f"Expected 404, got {response.status_code}"
    print("Test Success: Customer not found handled correctly.")


def test_activate_contract_success(customer_id, contract_id, base_url):
    url = f"{base_url}/{customer_id}/contracts/{contract_id}/activate"
    response = requests.put(url)
    assert response.status_code == 204, f"Expected 204, got {response.status_code}"
    print("Test Success: Contract activated successfully.")


def test_activate_contract_not_found(customer_id, base_url, invalid_contract_id):
    url = f"{base_url}/{customer_id}/contracts/{invalid_contract_id}/activate"
    response = requests.put(url)
    assert response.status_code == 404, f"Expected 404, got {response.status_code}"
    print("Test Success: Contract not found handled correctly.")