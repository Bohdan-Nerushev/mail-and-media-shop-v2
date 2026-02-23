package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class Product {

    @NotNull
    private final UUID id;

    @NotBlank
    @Size(min = 1, max = 100, message = "Product name must not exceed 100 characters")
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
        this.monthlyFee = monthlyFee;
    }

}
