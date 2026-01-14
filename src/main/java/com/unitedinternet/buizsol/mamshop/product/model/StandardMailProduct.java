package com.unitedinternet.buizsol.mamshop.product.model;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public class StandardMailProduct extends MailProduct {

    private static final BigDecimal FIXED_SETUP_FEE = new BigDecimal("4.99");
    private static final Long FIXED_STORAGE_SIZE = 4L;

    public StandardMailProduct(
            @NotBlank(message = "Product name must not be blank") final String name,
            @NotNull(message = "Brand must not be null") final Brand brand,
            @NotNull(message = "Monthly fee must not be null") final BigDecimal monthlyFee) {
        super(name, brand, FIXED_SETUP_FEE, monthlyFee, FIXED_STORAGE_SIZE);
    }
}
