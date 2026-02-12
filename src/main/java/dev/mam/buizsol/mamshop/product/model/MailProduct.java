package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateStorageSize;

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
        validateStorageSize(storageSizeGB);
        this.storageSize = storageSizeGB;
    }

    @NotNull
    public Long getStorageSize() {
        return storageSize;
    }
}
