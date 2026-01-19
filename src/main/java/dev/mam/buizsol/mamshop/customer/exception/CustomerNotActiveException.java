package dev.mam.buizsol.mamshop.customer.exception;

import jakarta.validation.constraints.NotNull;

public class CustomerNotActiveException extends Exception {

    public CustomerNotActiveException(@NotNull final String message) {
        super(message);
    }
}
