package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Address(
                @NotBlank @Size(max = 250) String street,
                @NotBlank @Size(max = 100) String number,
                @NotBlank @Size(max = 100) String postcode,
                @NotBlank @Size(max = 100) String city,
                @NotBlank @Size(max = 100) String country) {
}
