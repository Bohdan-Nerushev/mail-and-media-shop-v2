package com.unitedinternet.buizsol.mamshop.product.model;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PartnerProduct extends Product {

    public PartnerProduct(
            @NotBlank(message = "Product name must not be blank") final String name,
            @NotNull(message = "Brand must not be null") final Brand brand,
            @NotNull(message = "Setup fee must not be null") final BigDecimal setupFee,
            @NotNull(message = "Monthly fee must not be null") final BigDecimal monthlyFee) {
        super(
                name,
                brand,
                setupFee,
                monthlyFee);
    }
}
