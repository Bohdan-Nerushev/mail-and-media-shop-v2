package dev.mam.buizsol.mamshop.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CommunicationDetailsRequestDTO(
                @Email @NotBlank String email,
                @NotBlank String telephone) {
}
