package com.unitedinternet.buizsol.mamshop.billing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public final class InvoiceItem {

    @NotNull(message = "Product ID must not be null")
    private final UUID productId;

    @NotBlank(message = "Product name must not be blank")
    private final String productName;

    @NotNull(message = "Contract ID must not be null")
    private final UUID contractId;

    @NotNull(message = "Contract creation date must not be null")
    private final LocalDate contractCreationDate;

    @NotNull(message = "Setup fee must not be null")
    private final BigDecimal setupFee;

    @NotNull(message = "Monthly fee must not be null")
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
