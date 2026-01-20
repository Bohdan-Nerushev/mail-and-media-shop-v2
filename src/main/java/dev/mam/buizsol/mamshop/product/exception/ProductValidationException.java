package dev.mam.buizsol.mamshop.product.exception;

import jakarta.validation.constraints.NotNull;

public class ProductValidationException extends RuntimeException {

    public ProductValidationException(
            @NotNull final String message) {
        super(message);
    }
}
