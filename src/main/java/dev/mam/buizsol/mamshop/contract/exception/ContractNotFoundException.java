package dev.mam.buizsol.mamshop.contract.exception;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public class ContractNotFoundException extends Exception {

    public ContractNotFoundException(
            @NotNull(message = "Message must not be null") final String message) {
        super(message);
    }

    public ContractNotFoundException(
            @NotNull(message = "Message must not be null") final String message,
            @Nullable final Throwable cause) {
        super(message, cause);
    }
}
