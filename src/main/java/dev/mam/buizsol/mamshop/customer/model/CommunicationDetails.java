package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CommunicationDetails(
                @Email @NotBlank String email,
                @NotBlank String telephone) {
}
