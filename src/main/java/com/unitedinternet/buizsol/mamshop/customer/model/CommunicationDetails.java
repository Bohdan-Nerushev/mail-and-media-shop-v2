package com.unitedinternet.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommunicationDetails {

    @NotNull
    private final String email;
    @NotNull
    private final String telephone;

    public CommunicationDetails(
            @NotBlank(message = "Email must not be blank") String email,
            @NotBlank(message = "Telephone must not be blank") String telephone) {
        validate(email, "Email");
        validate(telephone, "Telephone");

        this.email = email;
        this.telephone = telephone;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @NotNull
    public String getTelephone() {
        return telephone;
    }

    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return "CommunicationDetails{" +
                "email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
