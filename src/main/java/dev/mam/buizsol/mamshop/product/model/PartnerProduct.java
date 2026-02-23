package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record PartnerProduct(
        @NotNull UUID id,
        @NotBlank @Size(max = 100, message = "Product name must not exceed 100 characters") String name,
        @NotNull Brand brand,
        @NotNull @DecimalMin(value = "0.00") BigDecimal setupFee,
        @NotNull @DecimalMin(value = "0.11") BigDecimal monthlyFee) implements Product {

    public PartnerProduct(
            @NotBlank @Size(max = 100) final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal setupFee,
            @NotNull final BigDecimal monthlyFee) {
        this(UUID.randomUUID(), name, brand, setupFee, monthlyFee);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Brand getBrand() {
        return brand;
    }

    @Override
    public BigDecimal getSetupFee() {
        return setupFee;
    }

    @Override
    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    @Override
    public PartnerProduct withMonthlyFee(
            @NotNull final BigDecimal monthlyFee) {
        return new PartnerProduct(id, name, brand, setupFee, monthlyFee);
    }
}
