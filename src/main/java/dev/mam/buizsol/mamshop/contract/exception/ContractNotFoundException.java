package dev.mam.buizsol.mamshop.contract.exception;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public class ContractNotFoundException extends Exception {

    public ContractNotFoundException(
            @NotNull final String message) {
        super(message);
    }

    public ContractNotFoundException(
            @NotNull final String message,
            @Nullable final Throwable cause) {
        super(message, cause);
    }
}
