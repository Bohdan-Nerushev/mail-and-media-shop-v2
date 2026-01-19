package com.unitedinternet.buizsol.mamshop.customer.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(
            @NotBlank final String message) {
        super(message);
    }
}
