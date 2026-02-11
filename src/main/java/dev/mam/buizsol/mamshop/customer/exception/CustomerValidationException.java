package dev.mam.buizsol.mamshop.customer.exception;

import jakarta.validation.constraints.NotNull;


public final class CustomerValidationException extends RuntimeException {

    public CustomerValidationException(
            @NotNull final String message) {
        super(message);
    }
}
