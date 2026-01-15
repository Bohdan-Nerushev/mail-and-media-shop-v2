package com.unitedinternet.buizsol.mamshop.contract.exception;

import jakarta.validation.constraints.NotNull;

public class CustomerNotActiveException extends RuntimeException {

    public CustomerNotActiveException(
            @NotNull final String message) {
        super(message);
    }
}
