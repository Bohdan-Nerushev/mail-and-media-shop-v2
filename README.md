# Mail & Media Shop API

REST API for managing customers, products, and contracts in the Mail & Media Shop platform.
Built with **Spring Boot** and documented via **Springdoc OpenAPI (Swagger UI)**.

---

## Table of Contents

- [Swagger UI](#swagger-ui)
- [API Overview](#api-overview)
  - [Customer API](#customer-api)
  - [Contract API](#contract-api)
  - [Product API](#product-api)
- [Common Response Schemas](#common-response-schemas)
- [Error Handling](#error-handling)

---

## Swagger UI

Interactive API documentation is available at:

```
http://localhost:8080/swagger-ui/index.html
```


## API Overview

### Base URL

```
http://localhost:8080/api/v1
```

## Customer API

**Tag:** `Customer`  
**Base path:** `/api/v1/customers`

Manages the full lifecycle of a customer: registration, retrieval, activation, and removal.
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
curl -X POST http://localhost:8080/api/v1/customers \
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
curl -X GET http://localhost:8080/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6
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

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `200`  | Customer found and returned      |
| `404`  | Customer not found               |
| `500`  | Unexpected internal server error |

---

### DELETE `/api/v1/customers/{customerId}` — Remove a Customer

Changes the status of the specified customer to `INACTIVE` (soft delete — the record is not physically deleted).

**HTTP Method:** `DELETE`

#### Path Parameters

| Parameter    | Type   | Required | Description                   |
|--------------|--------|----------|-------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Example Request (curl)

```bash
curl -X DELETE http://localhost:8080/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6
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
curl -X PUT http://localhost:8080/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6/activate
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
curl -X POST http://localhost:8080/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6/purchases \
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

Provides read access to contracts associated with a specific customer.

---

### GET `/api/v1/customers/{customerId}/contracts` — Load All Contracts for a Customer

Returns all contracts associated with the specified customer UUID.

**HTTP Method:** `GET`  
**Response Content-Type:** `application/json`

#### Path Parameters

| Parameter    | Type   | Required | Description                       |
|--------------|--------|----------|-----------------------------------|
| `customerId` | `UUID` | Yes      | Unique identifier of the customer |

#### Example Request (curl)

```bash
curl -X GET http://localhost:8080/api/v1/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6/contracts
```

#### Example Response — `200 OK`

```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "productId": "7cb65f12-1234-4abc-a789-000000000001",
    "creationDate": "2026-02-25",
    "status": "ACTIVE"
  },
  {
    "id": "b2c3d4e5-f6a7-8901-bcde-f01234567891",
    "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "productId": "8dc76g23-2345-5bcd-b890-111111111002",
    "creationDate": "2026-01-10",
    "status": "INACTIVE"
  }
]
```

> Returns an empty array `[]` if the customer exists but has no contracts.

#### Response Status Codes

| Status | Description                      |
|--------|----------------------------------|
| `200`  | Contracts returned successfully  |
| `404`  | Customer not found               |
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

#### Example Request (curl)

```bash
curl -X GET "http://localhost:8080/api/v1/products?brand=GMX"
```

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
  },
  {
    "id": "7cb65f12-1234-4abc-a789-000000000002",
    "name": "GMX MailPlus",
    "brand": "GMX",
    "setupFee": 9.99,
    "monthlyFee": 9.99,
    "storageSize": 524288
  }
]
```

> Returns an empty array `[]` if no products are found for the specified brand.

#### Response Status Codes

| Status | Description                                     |
|--------|-------------------------------------------------|
| `200`  | Products returned successfully                  |
| `400`  | Invalid or missing `brand` query parameter      |
| `404`  | Brand not found                                 |
| `500`  | Unexpected internal server error                |

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
