package dev.mam.buizsol.mamshop.shop.exception;

import jakarta.validation.constraints.NotNull;

public class CustomerAndProductBrandMismatchException extends Exception {

    public CustomerAndProductBrandMismatchException(
            @NotNull final String message) {
        super(message);
    }
}
