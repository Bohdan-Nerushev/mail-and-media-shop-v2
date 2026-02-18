package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PremiumMailProduct extends MailProduct {

    private static final BigDecimal FIXED_SETUP_FEE = new BigDecimal("9.99");
    private static final Long FIXED_STORAGE_SIZE = 8L;

    public PremiumMailProduct(
            @NotBlank @Size(max = 100) final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal monthlyFee) {
        super(name, brand, FIXED_SETUP_FEE, monthlyFee, FIXED_STORAGE_SIZE);
    }
}
