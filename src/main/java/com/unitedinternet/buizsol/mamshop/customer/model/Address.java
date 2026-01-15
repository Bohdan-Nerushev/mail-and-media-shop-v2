package com.unitedinternet.buizsol.mamshop.customer.model;

import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerValidationException;
import jakarta.validation.constraints.NotBlank;

public record Address(
        @NotBlank(message = "Street must not be blank") String street,
        @NotBlank(message = "Number must not be blank") String number,
        @NotBlank(message = "Postcode must not be blank") String postcode,
        @NotBlank(message = "City must not be blank") String city,
        @NotBlank(message = "Country must not be blank") String country) {

    public Address {
        validate(street, "Street");
        validate(number, "Number");
        validate(postcode, "Postcode");
        validate(city, "City");
        validate(country, "Country");
    }

    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new CustomerValidationException(fieldName + " must not be null or empty");
        }
    }
}
