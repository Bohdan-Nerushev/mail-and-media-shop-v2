package com.unitedinternet.buizsol.mamshop.product.model;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public abstract class MailProduct extends Product {

    @Min(value = 1, message = "Storage size must be at least 1GB")
    private final Long storageSize;

    protected MailProduct(
            @NotBlank(message = "Product name must not be blank") final String name,
            @NotNull(message = "Brand must not be null") final Brand brand,
            @NotNull(message = "Setup fee must not be null") final BigDecimal setupFee,
            @NotNull(message = "Monthly fee must not be null") final BigDecimal monthlyFee,
            @NotNull(message = "Storage size must not be null") final Long storageSize) {
        super(name, brand, setupFee, monthlyFee);
        this.storageSize = storageSize;
    }

    @NotNull
    public Long getStorageSize() {
        return storageSize;
    }
}
