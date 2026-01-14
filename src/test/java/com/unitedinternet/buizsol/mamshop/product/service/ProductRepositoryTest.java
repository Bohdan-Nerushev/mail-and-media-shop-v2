package com.unitedinternet.buizsol.mamshop.product.service;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.product.model.*;
import com.unitedinternet.buizsol.mamshop.product.service.ProductRepository;
import com.unitedinternet.buizsol.mamshop.product.service.ProductRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    private ProductRepository repository;

    private Product createMailProduct(
            String name,
            Brand brand,
            String setupFee,
            String monthlyFee,
            Long storageSize
    ) {
        return new MailProduct(
                name,
                brand, new BigDecimal(setupFee),
                new BigDecimal(monthlyFee),
                storageSize) {};
    }

    private Product createPartnerProduct(
            String name,
            Brand brand,
            String setupFee,
            String monthlyFee
    ) {
        return new PartnerProduct(
                name,
                brand,
                new BigDecimal(setupFee),
                new BigDecimal(monthlyFee));
    }

    private Product createBundleProduct(
            MailProduct mail,
            PartnerProduct partner
    ) {
        return new BundleProduct(mail, partner);
    }

    @BeforeEach
    void setUp() {
        repository = ProductRepositoryImpl.getInstance();
        repository.clearStorage();
    }

    @Test
    @DisplayName("01: Should save and retrieve product by ID")
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
    @DisplayName("02: Should return empty Optional when ID does not exist")
    void shouldReturnEmptyOptionalForNonExistentId() {
        Optional<Product> retrieved = repository.findById(UUID.randomUUID());
        assertTrue(retrieved.isEmpty());
    }

    @Test
    @DisplayName("03: Should throw IllegalArgumentException when saving null product")
    void shouldThrowExceptionWhenSavingNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("04: Should find products by brand")
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
    @DisplayName("05: Should return empty collection when brand has no products")
    void shouldReturnEmptyCollectionWhenBrandHasNoProducts() {
        Collection<Product> byBrand = repository.findByBrand(Brand.MAIL_COM);
        assertNotNull(byBrand);
        assertTrue(byBrand.isEmpty());
    }

    @Test
    @DisplayName("06: Should retrieve all products")
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
    @DisplayName("07: Should clear storage")
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
    @DisplayName("08: Should handle valid monthly fees at boundaries")
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
    @DisplayName("09: Should reject invalid monthly fees")
    void shouldRejectInvalidMonthlyFees(String monthlyFee) {
        assertThrows(IllegalArgumentException.class, () ->
                createMailProduct("MailInvalid", Brand.GMX, "1.00", monthlyFee, 2L));
    }

    @Test
    @DisplayName("10: Should throw exception when finding by null ID")
    void shouldThrowExceptionWhenFindingByNullId() {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
    }

    @Test
    @DisplayName("11: Should throw exception when finding by null brand")
    void shouldThrowExceptionWhenFindingByNullBrand() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByBrand(null));
    }

    @Test
    @DisplayName("12: Should handle BundleProduct creation and retrieval")
    void shouldHandleBundleProduct() {
        MailProduct mail = (MailProduct) createMailProduct("Mail", Brand.WEB_DE, "2.00", "1.00", 3L);
        PartnerProduct partner = (PartnerProduct) createPartnerProduct("Partner", Brand.WEB_DE, "3.00", "2.00");
        BundleProduct bundle = (BundleProduct) createBundleProduct(mail, partner);

        repository.save(bundle);

        Optional<Product> retrieved = repository.findById(bundle.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(bundle, retrieved.get());
        assertEquals(mail, ((BundleProduct) retrieved.get()).getMailProduct());
        assertEquals(partner, ((BundleProduct) retrieved.get()).getPartnerProduct());
    }

    @Test
    @DisplayName("13: Should reject BundleProduct with mismatched brands")
    void shouldRejectBundleWithMismatchedBrands() {
        MailProduct mail = (MailProduct) createMailProduct("Mail", Brand.GMX, "2.00", "1.00", 3L);
        PartnerProduct partner = (PartnerProduct) createPartnerProduct("Partner", Brand.WEB_DE, "3.00", "2.00");

        assertThrows(IllegalArgumentException.class, () -> createBundleProduct(mail, partner));
    }

    @Test
    @DisplayName("14: Should reject null in BundleProduct constructor")
    void shouldRejectNullInBundleConstructor() {
        MailProduct mail = null;
        PartnerProduct partner = (PartnerProduct) createPartnerProduct("Partner", Brand.MAIL_COM, "1.00", "0.50");
        assertThrows(IllegalArgumentException.class, () -> createBundleProduct(mail, partner));
    }

    @Test
    @DisplayName("15: Should reject MailProduct with invalid storage size")
    void shouldRejectMailProductWithInvalidStorageSize() {
        assertThrows(IllegalArgumentException.class, () ->
                createMailProduct("MailInvalid", Brand.GMX, "1.00", "0.50", 0L));
    }
}





