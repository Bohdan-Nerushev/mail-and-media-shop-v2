package dev.mam.buizsol.mamshop.billing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceItem(
        @NotNull UUID productId,

        @NotBlank @Size(min = 1, max = 150) String productName,

        @NotNull UUID contractId,

        @NotNull LocalDate contractCreationDate,

        @NotNull BigDecimal setupFee,

        @NotNull BigDecimal monthlyFee) {
}
