package com.unitedinternet.buizsol.mamshop.product.service;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.product.exception.ProductNotFoundException;
import com.unitedinternet.buizsol.mamshop.product.model.Product;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

final class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    private ProductServiceImpl(
            @NotNull final ProductRepository repository) {
        this.repository = repository;
    }

    private static final class Holder {
        private static final ProductServiceImpl INSTANCE = new ProductServiceImpl(ProductRepositoryImpl.getInstance());
    }

    @NotNull
    static ProductService getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void createProduct(
            @NotNull final Product product) {
        validateNotNull(product, "Product");
        repository.save(product);
    }

    @Override
    @NotNull
    public Optional<Product> findById(
            @NotNull final UUID id) {
        validateNotNull(id, "ID");
        return repository.findById(id);
    }

    @Override
    @NotNull
    public Collection<Product> findByBrand(
            @NotNull final Brand brand) {
        validateNotNull(brand, "Brand");
        return repository.findByBrand(brand);
    }

    @Override
    public void updateMonthlyFee(
            @NotNull final UUID id,
            @NotNull final BigDecimal monthlyFee) throws ProductNotFoundException {
        validateNotNull(id, "ID");
        validateNotNull(monthlyFee, "Monthly fee");

        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        product.setMonthlyFee(monthlyFee);
        repository.save(product);
    }

    private void validateNotNull(
            final Object value,
            final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
