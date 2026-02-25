package dev.mam.buizsol.mamshop.customer.exception;

import jakarta.validation.constraints.NotBlank;

public final class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(
            @NotBlank final String message) {
        super(message);
    }
}
