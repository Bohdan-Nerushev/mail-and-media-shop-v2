package dev.mam.buizsol.mamshop.billing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceItem(
        @NotNull UUID productId,

        @NotBlank String productName,

        @NotNull UUID contractId,

        @NotNull LocalDate contractCreationDate,

        @NotNull BigDecimal setupFee,

        @NotNull BigDecimal monthlyFee) {
}
