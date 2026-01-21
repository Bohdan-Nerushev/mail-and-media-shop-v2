package dev.mam.buizsol.mamshop.product.exception;

import jakarta.validation.constraints.NotBlank;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(
            @NotBlank final String message) {
        super(message);
    }
}
