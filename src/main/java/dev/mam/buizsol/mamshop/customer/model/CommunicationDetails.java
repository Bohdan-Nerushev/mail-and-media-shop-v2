package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommunicationDetails(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 5, max = 30) String telephone) {
}
