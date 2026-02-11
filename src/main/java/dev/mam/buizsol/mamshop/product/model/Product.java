package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateMonthlyFeeProduct;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotBlankProduct;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullProduct;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateSetupFeeProduct;

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

        validateNotBlankProduct(name, "Product name");
        validateNotNullProduct(brand, "Brand");
        validateNotNullProduct(setupFee, "Setup fee");
        validateNotNullProduct(monthlyFee, "Monthly fee");
        validateSetupFeeProduct(setupFee);
        validateMonthlyFeeProduct(monthlyFee);

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
        validateNotNullProduct(monthlyFee, "Monthly fee");
        validateMonthlyFeeProduct(monthlyFee);
        this.monthlyFee = monthlyFee;
    }

}
