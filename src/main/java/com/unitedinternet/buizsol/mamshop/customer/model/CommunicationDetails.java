package com.unitedinternet.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CommunicationDetails(
        @Email String email,
        @NotBlank String telephone) {

    public CommunicationDetails {
        validate(email, "Email");
        validate(telephone, "Telephone");
    }

    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
}
