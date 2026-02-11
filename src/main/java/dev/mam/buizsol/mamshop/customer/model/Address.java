package dev.mam.buizsol.mamshop.customer.model;

import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    private void validate(
            @Nullable final String value,
            @NotNull final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new CustomerValidationException(fieldName + " must not be null or empty");
        }
    }
}
