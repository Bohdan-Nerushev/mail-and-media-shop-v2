package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public abstract class MailProduct extends Product {

    @Min(1)
    private final Long storageSize;

    protected MailProduct(
            @NotBlank final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal setupFee,
            @NotNull final BigDecimal monthlyFee,
            @NotNull final Long storageSizeGB) {
        super(name, brand, setupFee, monthlyFee);
        validateNotNull(storageSizeGB, "Storage size");
        if (storageSizeGB < 1L) {
            throw new ProductValidationException("Storage size must be at least 1GB");
        }
        this.storageSize = storageSizeGB;
    }

    @NotNull
    public Long getStorageSize() {
        return storageSize;
    }
}
