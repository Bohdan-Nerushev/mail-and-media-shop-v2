package com.unitedinternet.buizsol.mamshop.customer.model;

import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerValidationException;
import jakarta.validation.constraints.NotBlank;

public record CommunicationDetails(
        @NotBlank(message = "Email must not be blank") String email,
        @NotBlank(message = "Telephone must not be blank") String telephone) {

    public CommunicationDetails {
        validate(email, "Email");
        validate(telephone, "Telephone");
    }

    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new CustomerValidationException(fieldName + " cannot be null or empty");
        }
    }
}
