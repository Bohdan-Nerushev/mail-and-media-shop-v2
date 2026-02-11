package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
final class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Autowired
    ProductServiceImpl(
            @NotNull final ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createProduct(@NotNull @Valid final Product product) {
        validateNotNull(product, "Product");
        repository.save(product);
    }

    @Override
    @NotNull
    public Optional<Product> findById(@NotNull final UUID productId) {
        validateNotNull(productId, "ID");
        return repository.findById(productId);
    }

    @Override
    @NotNull
    public List<Product> findByBrand(@NotNull final Brand brand) {
        validateNotNull(brand, "Brand");
        return List.copyOf(repository.findByBrand(brand));
    }

    @Override
    public void updateMonthlyFee(
            @NotNull final UUID id,
            @NotNull final BigDecimal monthlyFee) throws ProductNotFoundException {
        validateNotNull(id, "ID");
        notZeroOrNegative(monthlyFee, "Monthly fee");

        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        product.setMonthlyFee(monthlyFee);
        repository.save(product);
    }

    private void validateNotNull(
            @NotNull final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new ProductValidationException(fieldName + " must not be null");
        }
    }

    private void notZeroOrNegative(
            @Nullable final BigDecimal value,
            @NotNull final String fieldName) {
        if (value == null || value.compareTo(new BigDecimal("0.10")) <= 0) {
            throw new ProductValidationException(fieldName + " must be greater than 0.10 €");
        }
    }
}
