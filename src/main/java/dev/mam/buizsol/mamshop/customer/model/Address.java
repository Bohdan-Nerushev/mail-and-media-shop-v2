package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Address(
        @NotBlank String street,
        @NotBlank String number,
        @NotBlank String postcode,
        @NotBlank String city,
        @NotBlank String country) {
}
