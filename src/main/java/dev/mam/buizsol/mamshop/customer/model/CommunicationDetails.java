package dev.mam.buizsol.mamshop.customer.model;

import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommunicationDetails(
        @Email String email,
        @NotBlank String telephone) {

    public CommunicationDetails {
        validate(email, "Email");
        validate(telephone, "Telephone");
    }

    private void validate(
            @Nullable final String value,
            @NotNull final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new CustomerValidationException(fieldName + " cannot be null or empty");
        }
    }
}
