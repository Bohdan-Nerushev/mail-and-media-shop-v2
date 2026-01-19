package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
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
    @DecimalMin(value = "0.00", message = "Setup fee must not be negative")
    private final BigDecimal setupFee;

    @NotNull
    @DecimalMin(value = "0.11", message = "Monthly fee must be at least 0.11")
    private BigDecimal monthlyFee;

    protected Product(
            @NotBlank(message = "Product name must not be blank") final String name,
            @NotNull(message = "Brand must not be null") final Brand brand,
            @NotNull(message = "Setup fee must not be null") final BigDecimal setupFee,
            @NotNull(message = "Monthly fee must not be null") final BigDecimal monthlyFee) {

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
            throw new IllegalArgumentException("Monthly fee must be greater than 0.10 €");
        }
    }

    private void validateSetupFee(
            final BigDecimal setupFee) {
        if (setupFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Setup fee must not be negative");
        }
    }

    protected void validateNotBlank(
            @Nullable final String value,
            @NotNull final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    protected void validateNotNull(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
