package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class StandardMailProduct extends MailProduct {

    private static final BigDecimal FIXED_SETUP_FEE = new BigDecimal("4.99");
    private static final Long FIXED_STORAGE_SIZE = 4L;

    public StandardMailProduct(
            @NotBlank @Size(max = 100) final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal monthlyFee) {
        super(name, brand, FIXED_SETUP_FEE, monthlyFee, FIXED_STORAGE_SIZE);
    }
}
