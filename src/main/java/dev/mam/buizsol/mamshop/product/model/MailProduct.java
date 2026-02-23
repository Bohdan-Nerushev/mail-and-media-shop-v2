package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.validation.ValidStorageSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public abstract class MailProduct extends Product {

    @ValidStorageSize
    private final Long storageSize;

    protected MailProduct(
            @NotBlank @Size(max = 100) final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal setupFee,
            @NotNull final BigDecimal monthlyFee,
            @NotNull final Long storageSizeGB) {
        super(name, brand, setupFee, monthlyFee);
        this.storageSize = storageSizeGB;
    }

    @NotNull
    public Long getStorageSize() {
        return storageSize;
    }
}
