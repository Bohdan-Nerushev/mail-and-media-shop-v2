import requests

def get_user_token(
    kc_url: str,
    realm: str,
    client_id: str,
    client_secret: str,
    grant_type: str,
    username: str,
    password: str
) -> str:

    # Validate required input parameters
    if not kc_url:
        raise ValueError("kc_url must not be empty")
    if not realm:
        raise ValueError("realm must not be empty")
    if not client_id:
        raise ValueError("client_id must not be empty")
    if not client_secret:
        raise ValueError("client_secret must not be empty")
    if not grant_type:
        raise ValueError("grant_type must not be empty")
    if not username:
        raise ValueError("username must not be empty")
    if not password:
        raise ValueError("password must not be empty")

    url = f"{kc_url.rstrip('/')}/realms/{realm}/protocol/openid-connect/token"

    payload = {
        "client_id": client_id,
        "client_secret": client_secret,
        "grant_type": grant_type,
        "username": username,
        "password": password,
    }

    headers = {
        "Content-Type": "application/x-www-form-urlencoded",
    }

    try:
        response = requests.post(
            url=url,
            data=payload,
            headers=headers,
            timeout=15
        )
    except requests.RequestException as exc:
        raise ValueError(f"Request to Keycloak failed: {exc}") from exc

    try:
        response_data = response.json()
    except ValueError:
        raise ValueError(
            f"Keycloak returned non-JSON response: {response.text}"
        )

    if response.status_code != 200:
        raise ValueError(
            f"Keycloak token request failed. "
            f"HTTP {response.status_code}. Response: {response_data}"
        )

    access_token = response_data.get("access_token")
    if not access_token:
        raise ValueError(
            f"Keycloak response does not contain access_token. Response: {response_data}"
        )

    return access_token