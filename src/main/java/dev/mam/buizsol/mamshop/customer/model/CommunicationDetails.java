package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotBlankCustomer;

public record CommunicationDetails(
        @Email @NotBlank @Size(min = 1, max = 100) String email,
        @NotBlank @Size(min = 1, max = 100) String telephone) {

    public CommunicationDetails {
        validateNotBlankCustomer(email, "Email");
        validateNotBlankCustomer(telephone, "Telephone");
    }
}
