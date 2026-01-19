package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {

        @NotNull
        static ProductService getInstance() {
                return ProductServiceImpl.getInstance();
        }

        void createProduct(
                        @NotNull final Product product);

        @NotNull
        Optional<Product> findById(
                        @NotNull final UUID id);

        @NotNull
        Collection<Product> findByBrand(
                        @NotNull final Brand brand);

        void updateMonthlyFee(
                        @NotNull final UUID id,
                        @NotNull final BigDecimal monthlyFee) throws ProductNotFoundException;
}
