package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    void save(@NotNull @Valid Product product);

    void update(@NotNull @Valid Product product);

    @NotNull
    Optional<Product> findById(@NotNull UUID id);

    @NotNull
    Collection<Product> findByBrand(@NotNull Brand brand);

    @NotNull
    Collection<Product> findAll();

    void clearStorage();
}
