package com.unitedinternet.buizsol.mamshop.billing.exception;

import jakarta.validation.constraints.NotBlank;

public class InvoiceValidateException extends BillingException {

    public InvoiceValidateException(
            @NotBlank final String message) {
        super(message);
    }
}