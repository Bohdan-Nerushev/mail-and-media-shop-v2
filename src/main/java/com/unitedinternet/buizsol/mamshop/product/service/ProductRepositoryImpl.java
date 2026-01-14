package com.unitedinternet.buizsol.mamshop.product.service;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.product.model.Product;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

final class ProductRepositoryImpl implements ProductRepository {

    private final Map<UUID, Product> storage;

    private ProductRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    private static final class Holder {
        private static final ProductRepositoryImpl INSTANCE = new ProductRepositoryImpl();
    }

    @NotNull
    static ProductRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void save(
            @NotNull final Product product) {
        validateNotNull(product, "Product");
        storage.put(product.getId(), product);
    }

    @Override
    @NotNull
    public Optional<Product> findById(
            @NotNull final UUID id) {
        validateNotNull(id, "ID");
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    @NotNull
    public Collection<Product> findByBrand(
            @NotNull final Brand brand) {
        validateNotNull(brand, "Brand");
        return storage.values().stream()
                .filter(product -> product.getBrand() == brand)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    @NotNull
    public Collection<Product> findAll() {
        return Collections.unmodifiableCollection(storage.values());
    }

    private void validateNotNull(
            final Object value,
            final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
