package com.unitedinternet.buizsol.mamshop.product.exception;

import jakarta.validation.constraints.NotBlank;

public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(
            @NotBlank final String message) {
        super(message);
    }
}
