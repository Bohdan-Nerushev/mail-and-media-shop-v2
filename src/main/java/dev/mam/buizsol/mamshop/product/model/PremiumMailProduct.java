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

public record PremiumMailProduct(
        @NotNull UUID id,
        @NotBlank @Size(max = 100, message = "Product name must not exceed 100 characters") String name,
        @NotNull Brand brand,
        @NotNull @DecimalMin(value = "0.00") BigDecimal setupFee,
        @NotNull @DecimalMin(value = "0.11") BigDecimal monthlyFee,
        @NotNull @ValidStorageSize Long storageSize) implements Product {

    private static final BigDecimal FIXED_SETUP_FEE = new BigDecimal("9.99");
    private static final Long FIXED_STORAGE_SIZE = 8L;

    public PremiumMailProduct(
            @NotBlank @Size(min = 1, max = 100) final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal monthlyFee) {
        this(UUID.randomUUID(), name, brand, FIXED_SETUP_FEE, monthlyFee, FIXED_STORAGE_SIZE);
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

    public Optional<Long> getStorageSize() {
        return Optional.ofNullable(storageSize);
    }

    @Override
    public PremiumMailProduct withMonthlyFee(
            @NotNull final BigDecimal monthlyFee) {
        return new PremiumMailProduct(id, name, brand, setupFee, monthlyFee, storageSize);
    }
}
