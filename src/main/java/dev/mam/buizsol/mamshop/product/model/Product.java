package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class Product {

    @NotNull
    private final UUID id;

    @NotBlank
    private final String name;

    @NotNull
    private final Brand brand;

    @NotNull
    @DecimalMin(value = "0.00")
    private final BigDecimal setupFee;

    @NotNull
    @DecimalMin(value = "0.11")
    private BigDecimal monthlyFee;

    protected Product(
            @NotBlank final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal setupFee,
            @NotNull final BigDecimal monthlyFee) {

        validateNotBlank(name, "Product name");
        validateNotNull(brand, "Brand");
        validateNotNull(setupFee, "Setup fee");
        validateNotNull(monthlyFee, "Monthly fee");
        validateSetupFee(setupFee);
        validateMonthlyFee(monthlyFee);

        this.id = UUID.randomUUID();
        this.name = name;
        this.brand = brand;
        this.setupFee = setupFee;
        this.monthlyFee = monthlyFee;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Brand getBrand() {
        return brand;
    }

    @NotNull
    public BigDecimal getSetupFee() {
        return setupFee;
    }

    @NotNull
    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(
            @NotNull final BigDecimal monthlyFee) {
        validateNotNull(monthlyFee, "Monthly fee");
        validateMonthlyFee(monthlyFee);
        this.monthlyFee = monthlyFee;
    }

    private void validateMonthlyFee(
            @NotNull final BigDecimal monthlyFee) {
        if (monthlyFee.compareTo(new BigDecimal("0.10")) <= 0) {
            throw new ProductValidationException("Monthly fee must be greater than 0.10 €");
        }
    }

    private void validateSetupFee(
            final BigDecimal setupFee) {
        if (setupFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductValidationException("Setup fee must not be negative");
        }
    }

    protected void validateNotBlank(
            @Nullable final String value,
            @NotNull final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ProductValidationException(fieldName + " must not be null or empty");
        }
    }

    protected void validateNotNull(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new ProductValidationException(fieldName + " must not be null");
        }
    }
}
