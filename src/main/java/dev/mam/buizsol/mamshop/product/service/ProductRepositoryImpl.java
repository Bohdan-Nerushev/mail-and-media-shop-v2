package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import dev.mam.buizsol.mamshop.product.model.Product;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class ProductRepositoryImpl implements ProductRepository {

    private final Map<UUID, Product> storage;

    ProductRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public void save(final Product product) {
        if (product == null) {
            throw new ProductValidationException("Product must not be null");
        }
        storage.put(product.getId(), product);
    }

    @Override
    public void update(final Product product) {
        if (product == null) {
            throw new ProductValidationException("Product must not be null");
        }
        storage.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(final UUID id) {
        if (id == null) {
            throw new ProductValidationException("ID must not be null");
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Collection<Product> findByBrand(final Brand brand) {
        if (brand == null) {
            throw new ProductValidationException("Brand must not be null");
        }
        return storage.values().stream()
                .filter(product -> product.getBrand() == brand)
                .toList();
    }

    @Override
    public Collection<Product> findAll() {
        return Collections.unmodifiableCollection(storage.values());
    }

    @Override
    public void clearStorage() {
        storage.clear();
    }
}
