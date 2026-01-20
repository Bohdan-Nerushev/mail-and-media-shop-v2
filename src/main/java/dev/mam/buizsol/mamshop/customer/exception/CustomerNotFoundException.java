package dev.mam.buizsol.mamshop.customer.exception;

import jakarta.validation.constraints.NotNull;

public final class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(
            @NotNull final String message) {
        super(message);
    }
}
