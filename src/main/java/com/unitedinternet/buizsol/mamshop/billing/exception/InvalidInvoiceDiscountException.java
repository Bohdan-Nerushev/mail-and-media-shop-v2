package com.unitedinternet.buizsol.mamshop.billing.exception;

import jakarta.validation.constraints.NotBlank;

public class InvalidInvoiceDiscountException extends BillingException {

    public InvalidInvoiceDiscountException(
            @NotBlank final String message) {
        super(message);
    }
}
