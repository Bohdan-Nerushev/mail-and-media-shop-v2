package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PartnerProduct extends Product {

    public PartnerProduct(
            @NotBlank @Size(max = 100) final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal setupFee,
            @NotNull final BigDecimal monthlyFee) {
        super(
                name,
                brand,
                setupFee,
                monthlyFee);
    }
}
