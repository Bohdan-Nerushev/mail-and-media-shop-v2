package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import dev.mam.buizsol.mamshop.product.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Value("${billing.minimal-discount-amount}")
    private BigDecimal minimalDiscountAmount;

    ProductServiceImpl(final ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createProduct(final Product product) {
        repository.save(product);
    }

    @Override
    public Optional<Product> findById(final UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Product> findByBrand(final Brand brand) {
        return List.copyOf(repository.findByBrand(brand));
    }

    @Override
    public void updateMonthlyFee(final UUID id, final BigDecimal monthlyFee) throws ProductNotFoundException {
        if (monthlyFee.compareTo(minimalDiscountAmount) <= 0) {
            throw new ProductValidationException("Monthly fee must be greater than " + minimalDiscountAmount);
        }

        Product product = repository
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        product.setMonthlyFee(monthlyFee);
        repository.save(product);
    }
}
