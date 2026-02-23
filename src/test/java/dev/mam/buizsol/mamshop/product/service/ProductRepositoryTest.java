package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import dev.mam.buizsol.mamshop.product.model.BundleProduct;
import dev.mam.buizsol.mamshop.product.model.MailProduct;
import dev.mam.buizsol.mamshop.product.model.PartnerProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductRepository Tests")
class ProductRepositoryTest {

    private ProductRepository repository;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Product createMailProduct(
            String name,
            Brand brand,
            String setupFee,
            String monthlyFee,
            Long storageSize) {
        MailProduct product = new MailProduct(
                name,
                brand,
                new BigDecimal(setupFee),
                new BigDecimal(monthlyFee),
                storageSize);
        Set<ConstraintViolation<MailProduct>> violations = validator.validate(product);
        if (!violations.isEmpty()) {
            throw new ProductValidationException("Validation failed");
        }
        return product;
    }

    private Product createPartnerProduct(
            String name,
            Brand brand,
            String setupFee,
            String monthlyFee) {
        return new PartnerProduct(
                name,
                brand,
                new BigDecimal(setupFee),
                new BigDecimal(monthlyFee));
    }

    private Product createBundleProduct(
            MailProduct mail,
            PartnerProduct partner) {
        return new BundleProduct(mail, partner);
    }

    @BeforeEach
    void setUp() {
        repository = new ProductRepositoryImpl();
    }

    @Test
    @DisplayName("Should save and retrieve product by ID")
    void shouldSaveAndRetrieveProductById() {
        Product product = createMailProduct("Mail1",
                Brand.GMX,
                "1.00",
                "0.50",
                2L);
        repository.save(product);

        Optional<Product> retrieved = repository.findById(product.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(product, retrieved.get());
    }

    @Test
    @DisplayName("Should return empty Optional when ID does not exist")
    void shouldReturnEmptyOptionalForNonExistentId() {
        Optional<Product> retrieved = repository.findById(UUID.randomUUID());
        assertTrue(retrieved.isEmpty());
    }

    @Test
    @DisplayName("Should throw ProductValidationException when saving null product")
    void shouldThrowExceptionWhenSavingNullProduct() {
        assertThrows(ProductValidationException.class, () -> repository.save(null));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("Should find products by brand")
    void shouldFindProductsByBrand(Brand brand) {
        Product product1 = createMailProduct(
                brand + " Mail1",
                brand, "1.00",
                "0.50",
                2L);
        Product product2 = createPartnerProduct(
                brand + " Partner1",
                brand,
                "2.00",
                "1.00");
        repository.save(product1);
        repository.save(product2);

        Collection<Product> byBrand = repository.findByBrand(brand);
        assertEquals(2, byBrand.size());
        assertTrue(byBrand.contains(product1));
        assertTrue(byBrand.contains(product2));
    }

    @Test
    @DisplayName("Should return empty collection when brand has no products")
    void shouldReturnEmptyCollectionWhenBrandHasNoProducts() {
        Collection<Product> byBrand = repository.findByBrand(Brand.MAIL_COM);
        assertNotNull(byBrand);
        assertTrue(byBrand.isEmpty());
    }

    @Test
    @DisplayName("Should retrieve all products")
    void shouldRetrieveAllProducts() {
        Product p1 = createMailProduct("Mail1",
                Brand.GMX, "1.00",
                "0.50",
                2L);
        Product p2 = createPartnerProduct(
                "Partner1",
                Brand.WEB_DE,
                "2.00",
                "1.00");
        repository.save(p1);
        repository.save(p2);

        Collection<Product> all = repository.findAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(p1));
        assertTrue(all.contains(p2));
    }

    @Test
    @DisplayName("Should clear storage")
    void shouldClearStorage() {
        Product product = createMailProduct(
                "Mail1",
                Brand.GMX,
                "1.00",
                "0.50",
                2L);
        repository.save(product);

        repository.clearStorage();
        assertTrue(repository.findAll().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "0.11",
            "1000000.00"
    })
    @DisplayName("Should handle valid monthly fees at boundaries")
    void shouldHandleValidMonthlyFees(String monthlyFee) {
        Product product = createMailProduct("MailBoundary", Brand.MAIL_COM, "1.00", monthlyFee, 2L);
        repository.save(product);

        Optional<Product> retrieved = repository.findById(product.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(new BigDecimal(monthlyFee), retrieved.get().getMonthlyFee());
    }

    @ParameterizedTest
    @CsvSource({
            "0.10",
            "0.00",
            "-1.00"
    })
    @DisplayName("Should reject invalid monthly fees")
    void shouldRejectInvalidMonthlyFees(String monthlyFee) {
        assertThrows(ProductValidationException.class,
                () -> createMailProduct("MailInvalid", Brand.GMX, "1.00", monthlyFee, 2L));
    }

    @Test
    @DisplayName("Should throw exception when finding by null ID")
    void shouldThrowExceptionWhenFindingByNullId() {
        assertThrows(ProductValidationException.class, () -> repository.findById(null));
    }

    @Test
    @DisplayName("Should throw exception when finding by null brand")
    void shouldThrowExceptionWhenFindingByNullBrand() {
        assertThrows(ProductValidationException.class, () -> repository.findByBrand(null));
    }

    @Test
    @DisplayName("Should handle BundleProduct creation and retrieval")
    void shouldHandleBundleProduct() {
        MailProduct mail = (MailProduct) createMailProduct("Mail", Brand.WEB_DE, "2.00", "1.00", 3L);
        PartnerProduct partner = (PartnerProduct) createPartnerProduct("Partner", Brand.WEB_DE, "3.00", "2.00");
        BundleProduct bundle = (BundleProduct) createBundleProduct(mail, partner);

        repository.save(bundle);

        Optional<Product> retrieved = repository.findById(bundle.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(bundle, retrieved.get());
        assertEquals(mail, ((BundleProduct) retrieved.get()).mailProduct());
        assertEquals(partner, ((BundleProduct) retrieved.get()).partnerProduct());
    }

    @Test
    @DisplayName("Should reject BundleProduct with mismatched brands")
    void shouldRejectBundleWithMismatchedBrands() {
        MailProduct mail = (MailProduct) createMailProduct("Mail", Brand.GMX, "2.00", "1.00", 3L);
        PartnerProduct partner = (PartnerProduct) createPartnerProduct("Partner", Brand.WEB_DE, "3.00", "2.00");

        assertThrows(ProductValidationException.class, () -> createBundleProduct(mail, partner));
    }

    @Test
    @DisplayName("Should reject null in BundleProduct constructor")
    void shouldRejectNullInBundleConstructor() {
        MailProduct mail = null;
        PartnerProduct partner = (PartnerProduct) createPartnerProduct("Partner", Brand.MAIL_COM, "1.00", "0.50");
        assertThrows(ProductValidationException.class, () -> createBundleProduct(mail, partner));
    }

    @Test
    @DisplayName("Should reject MailProduct with invalid storage size")
    void shouldRejectMailProductWithInvalidStorageSize() {
        assertThrows(ProductValidationException.class,
                () -> createMailProduct("MailInvalid", Brand.GMX, "1.00", "0.50", 0L));
    }

    @Test
    @DisplayName("Should update existing product in storage")
    void shouldUpdateExistingProduct() {
        final MailProduct product = (MailProduct) createMailProduct("Initial Mail",
                Brand.GMX,
                "1.00",
                "0.50",
                2L);
        repository.save(product);

        final BigDecimal newMonthlyFee = new BigDecimal("0.75");
        final MailProduct updatedProduct = product.withMonthlyFee(newMonthlyFee);

        repository.update(updatedProduct);

        final Optional<Product> retrieved = repository.findById(product.getId());
        assertThat(retrieved)
                .isPresent()
                .hasValueSatisfying(updated -> {
                    assertThat(updated.getMonthlyFee())
                            .isEqualByComparingTo(newMonthlyFee);
                    assertThat(updated.getName())
                            .isEqualTo("Initial Mail");
                    assertThat(updated.getId())
                            .isEqualTo(product.getId());
                });
    }

    @Test
    @DisplayName("Should save product when updating non-existent product")
    void shouldSaveProductWhenUpdatingNonExistentProduct() {
        final Product product = createMailProduct("New Product",
                Brand.WEB_DE,
                "1.00",
                "0.60",
                3L);

        repository.update(product);

        final Optional<Product> retrieved = repository.findById(product.getId());
        assertThat(retrieved)
                .isPresent()
                .contains(product);
    }

    @Test
    @DisplayName("Should throw ProductValidationException when updating null product")
    void shouldThrowExceptionWhenUpdatingNullProduct() {
        assertThatThrownBy(() -> repository.update(null))
                .isInstanceOf(ProductValidationException.class)
                .hasMessage("Product must not be null");
    }
}
