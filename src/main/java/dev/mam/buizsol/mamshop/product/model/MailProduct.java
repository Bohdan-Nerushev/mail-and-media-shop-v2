package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.validation.ValidStorageSize;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public record MailProduct(
        @NotNull UUID id,
        @NotBlank @Size(max = 100, message = "Product name must not exceed 100 characters") String name,
        @NotNull Brand brand,
        @NotNull @DecimalMin(value = "0.00") BigDecimal setupFee,
        @NotNull @DecimalMin(value = "0.11") BigDecimal monthlyFee,
        @NotNull @ValidStorageSize Long storageSize) implements Product {

    public MailProduct(
            @NotBlank @Size(max = 100) final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal setupFee,
            @NotNull final BigDecimal monthlyFee,
            @NotNull final Long storageSize) {
        this(UUID.randomUUID(), name, brand, setupFee, monthlyFee, storageSize);
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
    @NotNull
    public Optional<Long> getStorageSize() {
        return Optional.ofNullable(storageSize);
    }

    @Override
    public MailProduct withMonthlyFee(
            @NotNull final BigDecimal monthlyFee) {
        return new MailProduct(id, name, brand, setupFee, monthlyFee, storageSize);
    }
}
