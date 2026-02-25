package dev.mam.buizsol.mamshop.config;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ErrorResponse(
        @NotNull String correlationId,
        @NotNull String errorCode,
        @NotBlank String message,
        @NotNull LocalDateTime timestamp) {
}
