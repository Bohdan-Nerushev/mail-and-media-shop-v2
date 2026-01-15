package com.unitedinternet.buizsol.mamshop.contract.exception;

import jakarta.validation.constraints.NotNull;

public class BrandMismatchException extends RuntimeException {

    public BrandMismatchException(
            @NotNull final String message) {
        super(message);
    }
}
