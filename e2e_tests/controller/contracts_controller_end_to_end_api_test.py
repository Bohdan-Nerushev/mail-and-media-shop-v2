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

def test_activate_contract_idempotency(customer_id, contract_id, base_url):
    url = f"{base_url}/{customer_id}/contracts/{contract_id}/activate"
    response = requests.put(url)
    assert response.status_code in [204, 409], f"Contract activation idempotency failed, got {response.status_code}"
    print("Test Success: Contract activation idempotency check passed.")

def test_activate_contract_forbidden(wrong_customer_id, contract_id, base_url):
    url = f"{base_url}/{wrong_customer_id}/contracts/{contract_id}/activate"
    response = requests.put(url)
    assert response.status_code in [400, 403, 404], f"Expected 400, 403 or 404 for wrong customer contract activation, got {response.status_code}"
    print("Test Success: Access control for contract activation passed.")

def test_get_contracts_inactive_customer(customer_id, base_url):
    url = f"{base_url}/{customer_id}/contracts"
    response = requests.get(url)

    assert response.status_code == 409, f"Expected 409 for inactive customer contracts retrieval, got {response.status_code}"
    print("Test Success: Retrieval of contracts for inactive customer handled (409) passed.")

def test_get_contracts_deleted_customer(customer_id, base_url):
    url = f"{base_url}/{customer_id}/contracts"
    response = requests.get(url)
    assert response.status_code == 404, f"Expected 404 for deleted customer contracts, got {response.status_code}"
    print("Test Success: Contracts not accessible for deleted customer passed.")