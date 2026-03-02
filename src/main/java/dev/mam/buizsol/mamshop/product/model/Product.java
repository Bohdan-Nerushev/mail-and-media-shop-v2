package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface Product {

    @NotNull
    UUID getId();

    @NotBlank
    @Size(min = 1, max = 100, message = "Product name must not exceed 100 characters")
    String getName();

    @NotNull
    Brand getBrand();

    @NotNull
    @DecimalMin(value = "0.00")
    BigDecimal getSetupFee();

    @NotNull
    @DecimalMin(value = "0.11")
    BigDecimal getMonthlyFee();

    @NotNull
    Product withMonthlyFee(@NotNull BigDecimal monthlyFee);

    @NotNull
    default Optional<Long> getStorageSize() {
        return Optional.empty();
    }

}
