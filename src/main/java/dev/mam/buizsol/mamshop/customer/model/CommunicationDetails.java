package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotBlankCustomer;

public record CommunicationDetails(
        @Email @NotBlank String email,
        @NotBlank String telephone) {

    public CommunicationDetails {
        validateNotBlankCustomer(email, "Email");
        validateNotBlankCustomer(telephone, "Telephone");
    }
}
