package com.unitedinternet.buizsol.mamshop.product.model;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public class PremiumMailProduct extends MailProduct {

    private static final BigDecimal FIXED_SETUP_FEE = new BigDecimal("9.99");
    private static final Long FIXED_STORAGE_SIZE = 8L;

    public PremiumMailProduct(
            @NotBlank(message = "Product name must not be blank") final String name,
            @NotNull(message = "Brand must not be null") final Brand brand,
            @NotNull(message = "Monthly fee must not be null") final BigDecimal monthlyFee) {
        super(name, brand, FIXED_SETUP_FEE, monthlyFee, FIXED_STORAGE_SIZE);
    }
}
