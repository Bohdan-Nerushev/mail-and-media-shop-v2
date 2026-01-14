package com.unitedinternet.buizsol.mamshop.product.service;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.product.model.Product;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

interface ProductRepository {

    void save(
            @NotNull final Product product);

    @NotNull
    Optional<Product> findById(
            @NotNull final UUID id);

    @NotNull
    Collection<Product> findByBrand(
            @NotNull final Brand brand);

    @NotNull
    Collection<Product> findAll();
}
