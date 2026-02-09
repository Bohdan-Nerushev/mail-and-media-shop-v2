package dev.mam.buizsol.mamshop.billing.exception;

import jakarta.validation.constraints.NotBlank;

public class InvoiceValidationException extends BillingException {

    public InvoiceValidationException(
            @NotBlank final String message) {
        super(message);
    }
}