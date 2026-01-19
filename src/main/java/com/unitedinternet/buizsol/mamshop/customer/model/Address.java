package com.unitedinternet.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotBlank;

public record Address(
        @NotBlank String street,
        @NotBlank String number,
        @NotBlank String postcode,
        @NotBlank String city,
        @NotBlank String country) {

    public Address {
        validate(street, "Street");
        validate(number, "Number");
        validate(postcode, "Postcode");
        validate(city, "City");
        validate(country, "Country");
    }

    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }
}
