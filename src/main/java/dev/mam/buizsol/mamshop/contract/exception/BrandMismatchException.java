package dev.mam.buizsol.mamshop.contract.exception;

import jakarta.validation.constraints.NotNull;

public class BrandMismatchException extends Exception {

    public BrandMismatchException(
            @NotNull final String message) {
        super(message);
    }
}
