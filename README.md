# Mail & Media Shop API

REST API for managing customers, products, and contracts in the Mail & Media Shop platform.
Built with **Spring Boot** and documented via **Springdoc OpenAPI (Swagger UI)**.

---

## Table of Contents

- [Swagger UI](#swagger-ui)
- [Testing](#testing)
- [Dockerization](#dockerization)
- [API Overview](#api-overview)
  - [Customer API](#customer-api)
  - [Contract API](#contract-api)
  - [Product API](#product-api)
  - [Billing API](#billing-api)
- [Common Response Schemas](#common-response-schemas)
- [Error Handling](#error-handling)

---
## Swagger UI

Interactive API documentation is available at:

```
http://localhost:8090/swagger-ui/index.html
```

## Testing

### Unit Tests (Java/JUnit)
To run unit tests, do:
```bash
mvn clean test
```

### End-to-End (E2E) Tests (Python)
To run the full test cycle (build, start server, run tests, stop) use:
```bash
chmod +x e2e_tests/run_e2e.sh
./e2e_tests/run_e2e.sh
```

 ---

For a local CI pipeline, do:
```bash
gitlab-ci-local
```

## Dockerization

The project uses **PostgreSQL 16** as the database.

### Exposed Ports
- **Application**: `8090` (maps to internal `8080`) can be changed in the .env file.
- **PostgreSQL**: `5430` (maps to internal `5432`) can be changed in the .env file.

### Running the entire project
```bash
docker compose up -d --build
```

### Stopping the project
```bash
docker compose down
```

### Viewing application logs
```bash
docker compose logs -f app
```

### Access the application container shell:
```bash
docker compose exec app bash
```

### Access the PostgreSQL container shell:
```bash
docker compose exec shop_db psql -U dev_user -d shop_db
```

### Restarting only the application (after code changes - rebuilds the app image)
```bash
docker compose up -d --build app
```

## API Overview

### Base URL

```
http://localhost:8090/api/v1
```

## Customer API

**Tag:** `Customer`  
**Base path:** `/api/v1/customers`

Manages the full lifecycle of a customer: registration, retrieval, activation, deactivation, and removal.
Also exposes the product purchase operation that creates a contract.

---

### POST `/api/v1/customers` — Register a New Customer

Creates a new customer record.

**HTTP Method:** `POST`  
**Request Content-Type:** `application/json`  
**Response Content-Type:** `application/json`

#### Request Body

| Field                  | Type                         | Required | Constraints                              |
|------------------------|------------------------------|----------|------------------------------------------|
| `firstName`            | `string`                     | Yes      | Not blank                                |
| `lastName`             | `string`                     | Yes      | Not blank                                |
| `birthDate`            | `string (LocalDate)`         | Yes      | Not null, format: `YYYY-MM-DD`           |
| `address`              | `AddressRequestDTO`          | Yes      | Not null, see sub-fields below           |
| `invoiceAddress`       | `AddressRequestDTO` / `null` | No       | Nullable, same structure as `address`    |
| `communicationDetails` | `CommunicationDetailsRequestDTO` | Yes  | Not null, see sub-fields below           |
| `brand`                | `string (Brand enum)`        | Yes      | One of: `GMX`, `WEB_DE`, `MAIL_COM`     |

**`AddressRequestDTO` fields:**

| Field      | Type     | Constraints         |
|------------|----------|---------------------|
| `street`   | `string` | Not blank, max 250  |
| `number`   | `string` | Not blank, max 100  |
| `postcode` | `string` | Not blank, max 100  |
| `city`     | `string` | Not blank, max 100  |
| `country`  | `string` | Not blank, max 100  |

**`CommunicationDetailsRequestDTO` fields:**

| Field       | Type     | Constraints          |
|-------------|----------|----------------------|
| `email`     | `string` | Not blank, valid email format |
| `telephone` | `string` | Not blank            |

#### Example Request (curl)

```bash
curl -X POST http://localhost:8090/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
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
    "invoiceAddress": null,
    "communicationDetails": {
      "email": "max.mustermann@gmx.de",
      "telephone": "+49 621 123456"
    },
    "brand": "GMX"
  }'
```

#### Example Response — `201 Created`

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
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
  "invoiceAddress": null,
  "communicationDetails": {
    "email": "max.mustermann@gmx.de",
    "telephone": "+49 621 123456"
  },
  "brand": "GMX",
  "status": "ACTIVE"
}
```

#### Response Status Codes

| Status | Description                                  |
|--------|----------------------------------------------|
| `201`  | Customer registered successfully             |
| `400`  | Validation failed — invalid or missing fields |
| `500`  | Unexpected internal server error              |

---

### GET `/api/v1/customers/{customerId}` — Load a Customer by ID

Returns the customer with the specified UUID.

**HTTP Method:** `GET`

#### Path Parameters

| Parameter    | Type   | Required | Description                   |
|--------------|--------|----------|-------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Example Request (curl)

```bash
curl -X GET http://localhost:8090/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

#### Example Response — `200 OK`

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
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
  "invoiceAddress": null,
  "communicationDetails": {
    "email": "max.mustermann@gmx.de",
    "telephone": "+49 621 123456"
  },
  "brand": "GMX",
  "status": "ACTIVE"
}
```
#### Status Examples

In responses, the `status` field indicates the current lifecycle state:

**Active Customer:**
```json
{ "id": "...", "status": "ACTIVE", ... }
```

**Inactive Customer (after deactivation):**
```json
{ "id": "...", "status": "INACTIVE", ... }
```


#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `200`  | Customer found and returned      |
| `404`  | Customer not found               |
| `500`  | Unexpected internal server error |

---

### DELETE `/api/v1/customers/{customerId}` — Remove a Customer

Deletes the customer by their `customerId`.

**HTTP Method:** `DELETE`

#### Path Parameters

| Parameter    | Type   | Required | Description                   |
|--------------|--------|----------|-------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Example Request (curl)

```bash
curl -X DELETE http://localhost:8090/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

#### Example Response — `204 No Content`

No response body is returned.

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `204`  | Customer removed successfully    |
| `404`  | Customer not found               |
| `500`  | Unexpected internal server error |

---

### PUT `/api/v1/customers/{customerId}/activate` — Activate a Customer

Changes the status of the specified customer to `ACTIVE`.

**HTTP Method:** `PUT`

#### Path Parameters

| Parameter    | Type   | Required | Description                   |
|--------------|--------|----------|-------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Example Request (curl)

```bash
curl -X PUT http://localhost:8090/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6/activate
```

#### Example Response — `204 No Content`

No response body is returned.

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `204`  | Customer activated successfully  |
| `404`  | Customer not found               |
| `500`  | Unexpected internal server error |

---

### PUT `/api/v1/customers/{customerId}/deactivate` — Deactivate a Customer

Changes the status of the specified customer to `INACTIVE`.

**HTTP Method:** `PUT`

#### Path Parameters

| Parameter    | Type   | Required | Description                   |
|--------------|--------|----------|-------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Example Request (curl)

```bash
curl -X PUT http://localhost:8090/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6/deactivate
```

#### Example Response — `204 No Content`

No response body is returned.

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `204`  | Customer deactivated successfully |
| `404`  | Customer not found               |
| `500`  | Unexpected internal server error |

---

### PUT `/api/v1/customers/{customerId}/address` — Update Customer Address

Updates the primary address of the customer.

**HTTP Method:** `PUT`  
**Request Content-Type:** `application/json`

#### Path Parameters

| Parameter    | Type   | Required | Description                   |
|--------------|--------|----------|-------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Request Body
Standard `AddressRequestDTO` structure.

#### Example Request (curl)

```bash
curl -X PUT http://localhost:8090/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6/address \
  -H "Content-Type: application/json" \
  -d '{
    "street": "Neustraße",
    "number": "5",
    "postcode": "10115",
    "city": "Berlin",
    "country": "Germany"
  }'
```

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `204`  | Address updated successfully     |
| `404`  | Customer not found               |
| `400`  | Invalid address data             |
| `500`  | Unexpected internal server error |

---

### PUT `/api/v1/customers/{customerId}/invoice-address` — Update Invoice Address

Updates the invoice address of the customer.

**HTTP Method:** `PUT`  
**Request Content-Type:** `application/json`

#### Path Parameters

| Parameter    | Type   | Required | Description                   |
|--------------|--------|----------|-------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Request Body
Standard `AddressRequestDTO` structure.

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `204`  | Invoice address updated successfully |
| `404`  | Customer not found               |
| `400`  | Invalid address data             |
| `500`  | Unexpected internal server error |

---

### PUT `/api/v1/customers/{customerId}/communication-details` — Update Communication Details

Updates the email and telephone for a customer.

**HTTP Method:** `PUT`  
**Request Content-Type:** `application/json`

#### Path Parameters

| Parameter    | Type   | Required | Description                   |
|--------------|--------|----------|-------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Request Body
Standard `CommunicationDetailsRequestDTO` structure.

#### Response Status Codes

| Status | Description                                  |
|--------|----------------------------------------------|
| `204`  | Communication details updated successfully  |
| `404`  | Customer not found                           |
| `400`  | Invalid communication data                   |
| `500`  | Unexpected internal server error              |

---

### POST `/api/v1/customers/{customerId}/purchases` — Purchase a Product

Creates a new contract linking the specified customer to a selected product.

**HTTP Method:** `POST`  
**Request Content-Type:** `application/json`  
**Response Content-Type:** `application/json`

#### Path Parameters

| Parameter    | Type   | Required | Description                          |
|--------------|--------|----------|--------------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer    |

#### Request Body

| Field       | Type   | Required | Constraints                      |
|-------------|--------|----------|----------------------------------|
| `productId` | `UUID` | Yes      | Not null, UUID of the product    |

#### Example Request (curl)

```bash
curl -X POST http://localhost:8090/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6/purchases \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "7cb65f12-1234-4abc-a789-000000000001"
  }'
```

#### Example Response — `201 Created`

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "productId": "7cb65f12-1234-4abc-a789-000000000001",
  "creationDate": "2026-02-25",
  "status": "ACTIVE"
}
```

#### Response Status Codes

| Status | Description                                        |
|--------|----------------------------------------------------|
| `201`  | Contract created successfully                      |
| `400`  | Validation failed — invalid or missing `productId` |
| `404`  | Customer or product not found                      |
| `500`  | Unexpected internal server error                   |

---

## Contract API

**Tag:** `Contract`  
**Base path:** `/api/v1/customers/{customerId}/contracts`

Provides management and read access to contracts associated with a specific customer.

---

### GET `/api/v1/customers/{customerId}/contracts` — Load All Contracts for a Customer

Returns all contracts associated with the specified customer UUID.

**HTTP Method:** `GET`  
**Response Content-Type:** `application/json`

#### Path Parameters

| Parameter    | Type   | Required | Description                       |
|--------------|--------|----------|-----------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Example Response — `200 OK`

```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "productId": "7cb65f12-1234-4abc-a789-000000000001",
    "creationDate": "2026-02-25",
    "status": "ACTIVE"
  }
]
```

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `200`  | Contracts returned successfully  |
| `404`  | Customer not found               |
| `500`  | Unexpected internal server error |

---

### PUT `/api/v1/customers/{customerId}/contracts/{contractId}/activate` — Activate a Contract

Changes the status of a specific contract to `ACTIVE`.

**HTTP Method:** `PUT`

#### Path Parameters

| Parameter    | Type   | Required | Description                        |
|--------------|--------|----------|------------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer  |
| `contractId` | `UUID` | Yes      | Unique identifier of the contract  |

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `204`  | Contract activated successfully |
| `404`  | Contract not found               |
| `500`  | Unexpected internal server error |

---

## Product API

**Tag:** `Product`  
**Base path:** `/api/v1/products`

Provides read access to the product catalog, filtered by brand.

---

### GET `/api/v1/products` — Load All Products for a Brand

Returns all products available for the specified brand.

**HTTP Method:** `GET`  
**Response Content-Type:** `application/json`

#### Query Parameters

| Parameter | Type               | Required | Description                                      |
|-----------|--------------------|----------|--------------------------------------------------|
| `brand`   | `string (Brand enum)` | Yes   | One of: `GMX`, `WEB_DE`, `MAIL_COM`             |

#### Example Response — `200 OK`

```json
[
  {
    "id": "7cb65f12-1234-4abc-a789-000000000001",
    "name": "GMX ProMail",
    "brand": "GMX",
    "setupFee": 0.00,
    "monthlyFee": 4.99,
    "storageSize": 65536
  }
]
```

#### Response Status Codes

| Status | Description                                     |
|--------|-------------------------------------------------|
| `200`  | Products returned successfully                  |
| `400`  | Invalid or missing `brand` query parameter      |
| `404`  | Brand not found                                 |
| `500`  | Unexpected internal server error                |

---

## Billing API

**Tag:** `Billing`  
**Base path:** `/api/v1/billing`

Handles invoice generation for customers.

---

### POST `/api/v1/billing/{customerId}/invoice` — Generate Invoice for a Customer

Generates an invoice for the specified customer based on their current active contracts.

**HTTP Method:** `POST`  
**Response Content-Type:** `application/json`

#### Path Parameters

| Parameter    | Type   | Required | Description                       |
|--------------|--------|----------|-----------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Example Response — `200 OK`

```json
{
  "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "invoiceDate": "2026-02-26",
  "totalAmount": 14.98,
  "items": [
    {
      "description": "GMX ProMail",
      "amount": 4.99
    }
  ]
}
```

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `200`  | Invoice generated successfully   |
| `404`  | Customer not found               |
| `500`  | Unexpected internal server error |

---

## Common Response Schemas

### `CustomerResponseDTO`

```json
{
  "id": "UUID",
  "firstName": "string",
  "lastName": "string",
  "birthDate": "YYYY-MM-DD",
  "address": "AddressRequestDTO",
  "invoiceAddress": "AddressRequestDTO | null",
  "communicationDetails": "CommunicationDetailsRequestDTO",
  "brand": "GMX | WEB_DE | MAIL_COM",
  "status": "ACTIVE | INACTIVE"
}
```

### `ContractResponseDTO`

```json
{
  "id": "UUID",
  "customerId": "UUID",
  "productId": "UUID",
  "creationDate": "YYYY-MM-DD",
  "status": "ACTIVE | INACTIVE"
}
```

### `ProductResponseDTO`

```json
{
  "id": "UUID",
  "name": "string",
  "brand": "GMX | WEB_DE | MAIL_COM",
  "setupFee": "decimal",
  "monthlyFee": "decimal",
  "storageSize": "long (KB)"
}
```

---

## Error Handling

All error responses share a unified structure defined by `ErrorResponse`:

```json
{
  "correlationId": "string (UUID)",
  "errorCode": "string",
  "message": "string",
  "timestamp": "YYYY-MM-DDTHH:MM:SS"
}
```

| Field           | Description                                                  |
|-----------------|--------------------------------------------------------------|
| `correlationId` | Unique identifier for the request — use for log traceability |
| `errorCode`     | Machine-readable error classification code                   |
| `message`       | Human-readable description of what went wrong                |
| `timestamp`     | UTC timestamp of when the error occurred                     |

### Common HTTP Status Codes

| Status | Meaning                                                         |
|--------|-----------------------------------------------------------------|
| `200`  | OK — request completed successfully                             |
| `201`  | Created — resource was created successfully                     |
| `204`  | No Content — operation completed, no body returned              |
| `400`  | Bad Request — validation failed or malformed input              |
| `404`  | Not Found — the requested resource does not exist               |
| `500`  | Internal Server Error — unexpected server-side failure          |
