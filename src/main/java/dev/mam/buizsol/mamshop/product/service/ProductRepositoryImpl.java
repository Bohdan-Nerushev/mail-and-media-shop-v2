package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullProduct;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
final class ProductRepositoryImpl implements ProductRepository {

    private final Map<UUID, Product> storage;

    ProductRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public void save(@NotNull @Valid final Product product) {
        validateNotNullProduct(product, "Product");
        storage.put(product.getId(), product);
    }

    @Override
    public void update(@NotNull @Valid final Product product) {
        validateNotNullProduct(product, "Product");
        storage.put(product.getId(), product);
    }

    @Override
    @NotNull
    public Optional<Product> findById(@NotNull final UUID id) {
        validateNotNullProduct(id, "ID");
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    @NotNull
    public Collection<Product> findByBrand(@NotNull final Brand brand) {
        validateNotNullProduct(brand, "Brand");
        return storage.values().stream()
                .filter(product -> product.getBrand() == brand)
                .toList();
    }

    @Override
    @NotNull
    public Collection<Product> findAll() {
        return Collections.unmodifiableCollection(storage.values());
    }

    @Override
    public void clearStorage() {
        storage.clear();
    }
}
