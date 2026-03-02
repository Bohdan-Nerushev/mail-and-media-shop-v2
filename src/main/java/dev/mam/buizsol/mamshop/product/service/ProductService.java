package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;

@Validated
public interface ProductService {

        void createProduct(@NotNull @Valid final Product product);

        @NotNull
        Optional<Product> findById(@NotNull final UUID id);

        @NotNull
        List<Product> findByBrand(@NotNull final Brand brand);

        void updateMonthlyFee(
                        @NotNull final UUID id,
                        @NotNull @DecimalMin(value = "0.10", inclusive = false) final BigDecimal monthlyFee)
                        throws ProductNotFoundException;
}
