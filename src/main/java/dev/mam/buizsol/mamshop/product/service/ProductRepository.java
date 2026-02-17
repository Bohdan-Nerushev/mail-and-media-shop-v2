package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

        void save(final Product product);

        void update(final Product product);

        Optional<Product> findById(final UUID id);

        Collection<Product> findByBrand(final Brand brand);

        Collection<Product> findAll();

        void clearStorage();
}
