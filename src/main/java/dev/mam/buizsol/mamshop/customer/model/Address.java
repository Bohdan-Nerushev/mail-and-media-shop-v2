package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotBlank;

public record Address(
        @NotBlank String street,
        @NotBlank String number,
        @NotBlank String postcode,
        @NotBlank String city,
        @NotBlank String country) {
}
