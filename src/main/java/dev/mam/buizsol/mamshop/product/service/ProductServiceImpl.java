package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullProduct;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateMonthlyFeeProduct;
import org.springframework.validation.annotation.Validated;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
final class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    ProductServiceImpl(
            @NotNull final ProductRepository repository) {
        validateNotNullProduct(repository, "Repository");
        this.repository = repository;
    }

    @Override
    public void createProduct(@NotNull @Valid final Product product) {
        validateNotNullProduct(product, "Product");
        repository.save(product);
    }

    @Override
    @NotNull
    public Optional<Product> findById(@NotNull final UUID productId) {
        validateNotNullProduct(productId, "ID");
        return repository.findById(productId);
    }

    @Override
    @NotNull
    public List<Product> findByBrand(@NotNull final Brand brand) {
        validateNotNullProduct(brand, "Brand");
        return List.copyOf(repository.findByBrand(brand));
    }

    @Override
    public void updateMonthlyFee(
            @NotNull final UUID id,
            @NotNull final BigDecimal monthlyFee) throws ProductNotFoundException {
        validateNotNullProduct(id, "ID");
        validateMonthlyFeeProduct(monthlyFee);

        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        product.setMonthlyFee(monthlyFee);
        repository.save(product);
    }
}
