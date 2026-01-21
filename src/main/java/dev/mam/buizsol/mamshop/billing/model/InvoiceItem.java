package dev.mam.buizsol.mamshop.billing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class InvoiceItem {

    @NotNull
    private final UUID productId;

    @NotBlank
    private final String productName;

    @NotNull
    private final UUID contractId;

    @NotNull
    private final LocalDate contractCreationDate;

    @NotNull
    private final BigDecimal setupFee;

    @NotNull
    private final BigDecimal monthlyFee;

    public InvoiceItem(
            @NotNull UUID productId,
            @NotBlank String productName,
            @NotNull UUID contractId,
            @NotNull LocalDate contractCreationDate,
            @NotNull BigDecimal setupFee,
            @NotNull BigDecimal monthlyFee) {
        this.productId = productId;
        this.productName = productName;
        this.contractId = contractId;
        this.contractCreationDate = contractCreationDate;
        this.setupFee = setupFee;
        this.monthlyFee = monthlyFee;
    }

    @NotNull
    public UUID getProductId() {
        return productId;
    }

    @NotBlank
    public String getProductName() {
        return productName;
    }

    @NotNull
    public UUID getContractId() {
        return contractId;
    }

    @NotNull
    public LocalDate getContractCreationDate() {
        return contractCreationDate;
    }

    @NotNull
    public BigDecimal getSetupFee() {
        return setupFee;
    }

    @NotNull
    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }
}
