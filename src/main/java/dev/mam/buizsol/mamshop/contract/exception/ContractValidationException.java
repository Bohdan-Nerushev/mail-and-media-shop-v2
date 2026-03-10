package dev.mam.buizsol.mamshop.contract.exception;

import jakarta.validation.constraints.NotNull;

public final class ContractValidationException extends RuntimeException {

    public ContractValidationException(@NotNull final String message) {
        super(message);
    }
}
