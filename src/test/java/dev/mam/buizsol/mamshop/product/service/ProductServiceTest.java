package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.MailProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

        private ProductService productService;

        private Product createDefaultProduct(
                        final String name,
                        final Brand brand,
                        final String setupFee,
                        final String monthlyFee) {
                return new MailProduct(
                                name,
                                brand,
                                new BigDecimal(setupFee),
                                new BigDecimal(monthlyFee),
                                1024L) {
                };
        }

        @BeforeEach
        void setUp() {
                productService = ProductService.getInstance();
                ((ProductRepositoryImpl) ProductRepositoryImpl.getInstance()).clearStorage();
        }

        @Test
        @DisplayName("01: Should successfully create product and retrieve by ID")
        void shouldSuccessfullyCreateProductAndRetrieveById() {
                final Product product = createDefaultProduct(
                                "Test Product",
                                Brand.GMX,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final Optional<Product> foundProduct = productService.findById(product.getId());

                assertTrue(foundProduct.isPresent());
                assertEquals(product, foundProduct.get());
                assertEquals(product.getId(), foundProduct.get().getId());
                assertEquals(product.getName(), foundProduct.get().getName());
                assertEquals(product.getBrand(), foundProduct.get().getBrand());
        }

        @Test
        @DisplayName("02: Should return empty Optional when product ID does not exist")
        void shouldReturnEmptyOptionalWhenProductIdDoesNotExist() {
                final UUID nonExistentId = UUID.randomUUID();

                final Optional<Product> foundProduct = productService.findById(nonExistentId);

                assertTrue(foundProduct.isEmpty());
        }

        @Test
        @DisplayName("03: Should throw IllegalArgumentException when creating product with null value")
        void shouldThrowIllegalArgumentExceptionWhenCreatingProductWithNullValue() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.createProduct(null));
        }

        @Test
        @DisplayName("04: Should throw IllegalArgumentException when finding product by null ID")
        void shouldThrowIllegalArgumentExceptionWhenFindingProductByNullId() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.findById(null));
        }

        @Test
        @DisplayName("05: Should find all products of specific brand")
        void shouldFindAllProductsOfSpecificBrand() {
                final Product gmxProduct1 = createDefaultProduct(
                                "GMX Product 1",
                                Brand.GMX,
                                "1.00",
                                "0.50");
                final Product gmxProduct2 = createDefaultProduct(
                                "GMX Product 2",
                                Brand.GMX,
                                "2.00",
                                "0.60");
                final Product webDeProduct = createDefaultProduct(
                                "WEB.DE Product",
                                Brand.WEB_DE,
                                "3.00",
                                "0.70");

                productService.createProduct(gmxProduct1);
                productService.createProduct(gmxProduct2);
                productService.createProduct(webDeProduct);

                final Collection<Product> gmxProducts = productService.findByBrand(Brand.GMX);

                assertEquals(2, gmxProducts.size());
                assertTrue(gmxProducts.contains(gmxProduct1));
                assertTrue(gmxProducts.contains(gmxProduct2));
                assertFalse(gmxProducts.contains(webDeProduct));
                assertTrue(gmxProducts.stream().allMatch(p -> p.getBrand() == Brand.GMX));
        }

        @Test
        @DisplayName("06: Should return empty collection when no products exist for brand")
        void shouldReturnEmptyCollectionWhenNoProductsExistForBrand() {
                final Product gmxProduct = createDefaultProduct(
                                "GMX Product",
                                Brand.GMX,
                                "1.00",
                                "0.50");
                productService.createProduct(gmxProduct);

                final Collection<Product> mailComProducts = productService.findByBrand(Brand.MAIL_COM);

                assertNotNull(mailComProducts);
                assertTrue(mailComProducts.isEmpty());
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("07: Should handle search for all available brands")
        void shouldHandleSearchForAllAvailableBrands(final Brand brand) {
                final Product product = createDefaultProduct(
                                brand.name() + " Product",
                                brand,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final Collection<Product> products = productService.findByBrand(brand);

                assertNotNull(products);
                assertFalse(products.isEmpty());
                assertTrue(products.stream().allMatch(p -> p.getBrand() == brand));
        }

        @Test
        @DisplayName("08: Should throw IllegalArgumentException when searching by null brand")
        void shouldThrowIllegalArgumentExceptionWhenSearchingByNullBrand() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.findByBrand(null));
        }

        @Test
        @DisplayName("09: Should successfully update monthly fee to valid value")
        void shouldSuccessfullyUpdateMonthlyFeeToValidValue() throws Exception {
                final Product product = createDefaultProduct(
                                "Fee Test",
                                Brand.MAIL_COM,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final BigDecimal newFee = new BigDecimal("0.75");
                productService.updateMonthlyFee(product.getId(), newFee);

                final Product updatedProduct = productService.findById(product.getId()).get();
                assertEquals(newFee, updatedProduct.getMonthlyFee());
        }

        @Test
        @DisplayName("10: Should successfully update monthly fee to minimum valid boundary value")
        void shouldSuccessfullyUpdateMonthlyFeeToMinimumValidBoundaryValue() throws Exception {
                final Product product = createDefaultProduct(
                                "Boundary Test",
                                Brand.GMX,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final BigDecimal minimumValidFee = new BigDecimal("0.11");
                productService.updateMonthlyFee(product.getId(), minimumValidFee);

                final Product updatedProduct = productService.findById(product.getId()).get();
                assertEquals(minimumValidFee, updatedProduct.getMonthlyFee());
        }

        @ParameterizedTest
        @CsvSource({
                        "0.11, GMX",
                        "0.50, WEB_DE",
                        "1.00, MAIL_COM",
                        "5.99, GMX",
                        "10.00, WEB_DE",
                        "99.99, MAIL_COM"
        })
        @DisplayName("11: Should update monthly fee for various valid values and brands")
        void shouldUpdateMonthlyFeeForVariousValidValuesAndBrands(
                        final String feeValue,
                        final Brand brand) throws Exception {
                final Product product = createDefaultProduct(
                                "Parameterized Test",
                                brand,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final BigDecimal newFee = new BigDecimal(feeValue);
                productService.updateMonthlyFee(product.getId(), newFee);

                final Product updatedProduct = productService.findById(product.getId()).get();
                assertEquals(0, newFee.compareTo(updatedProduct.getMonthlyFee()));
        }

        @Test
        @DisplayName("12: Should update monthly fee without affecting other products")
        void shouldUpdateMonthlyFeeWithoutAffectingOtherProducts() throws Exception {
                final Product product1 = createDefaultProduct(
                                "Product 1",
                                Brand.GMX,
                                "1.00",
                                "0.50");
                final Product product2 = createDefaultProduct(
                                "Product 2",
                                Brand.WEB_DE,
                                "2.00",
                                "0.60");
                productService.createProduct(product1);
                productService.createProduct(product2);

                final BigDecimal originalFeeProduct2 = product2.getMonthlyFee();
                final BigDecimal newFeeProduct1 = new BigDecimal("0.99");

                productService.updateMonthlyFee(product1.getId(), newFeeProduct1);

                final Product updatedProduct1 = productService.findById(product1.getId()).get();
                final Product unchangedProduct2 = productService.findById(product2.getId()).get();

                assertEquals(newFeeProduct1, updatedProduct1.getMonthlyFee());
                assertEquals(originalFeeProduct2, unchangedProduct2.getMonthlyFee());
        }

        @ParameterizedTest
        @CsvSource({
                        "0.10",
                        "0.09",
                        "0.05",
                        "0.01",
                        "0.00"
        })
        @DisplayName("13: Should throw IllegalArgumentException when monthly fee is at or below minimum threshold")
        void shouldThrowIllegalArgumentExceptionWhenMonthlyFeeIsAtOrBelowMinimumThreshold(
                        final String feeValue) throws Exception {
                final Product product = createDefaultProduct(
                                "Low Fee Test",
                                Brand.GMX,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final BigDecimal invalidFee = new BigDecimal(feeValue);

                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.updateMonthlyFee(product.getId(), invalidFee));

                assertNotNull(exception.getMessage());
                assertTrue(exception.getMessage().contains("0.10"));
        }

        @ParameterizedTest
        @CsvSource({
                        "-0.01",
                        "-0.10",
                        "-1.00",
                        "-10.00",
                        "-99.99"
        })
        @DisplayName("14: Should throw IllegalArgumentException when monthly fee is negative")
        void shouldThrowIllegalArgumentExceptionWhenMonthlyFeeIsNegative(final String feeValue) throws Exception {
                final Product product = createDefaultProduct(
                                "Negative Fee Test",
                                Brand.WEB_DE,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final BigDecimal negativeFee = new BigDecimal(feeValue);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.updateMonthlyFee(product.getId(), negativeFee));
        }

        @Test
        @DisplayName("15: Should throw ProductNotFoundException when updating non-existent product")
        void shouldThrowProductNotFoundExceptionWhenUpdatingNonExistentProduct() {
                final UUID nonExistentId = UUID.randomUUID();
                final BigDecimal validFee = new BigDecimal("1.00");

                final ProductNotFoundException exception = assertThrows(
                                ProductNotFoundException.class,
                                () -> productService.updateMonthlyFee(nonExistentId, validFee));

                assertNotNull(exception.getMessage());
                assertTrue(exception.getMessage().contains(nonExistentId.toString()));
        }

        @Test
        @DisplayName("16: Should throw IllegalArgumentException when product ID is null")
        void shouldThrowIllegalArgumentExceptionWhenProductIdIsNull() throws Exception {
                final BigDecimal validFee = new BigDecimal("1.00");

                assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.updateMonthlyFee(null, validFee));
        }

        @Test
        @DisplayName("17: Should throw IllegalArgumentException when monthly fee is null")
        void shouldThrowIllegalArgumentExceptionWhenMonthlyFeeIsNull() throws Exception {
                final Product product = createDefaultProduct(
                                "Null Fee Test",
                                Brand.MAIL_COM,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.updateMonthlyFee(product.getId(), null));
        }

        @Test
        @DisplayName("18: Should throw IllegalArgumentException when both ID and fee are null")
        void shouldThrowIllegalArgumentExceptionWhenBothIdAndFeeAreNull() throws Exception {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.updateMonthlyFee(null, null));
        }

        @Test
        @DisplayName("20: Should handle minimum valid monthly fee boundary (0.11)")
        void shouldHandleMinimumValidMonthlyFeeBoundary() {
                final BigDecimal minimumValidFee = new BigDecimal("0.11");
                final Product product = createDefaultProduct(
                                "Minimum Fee Product",
                                Brand.GMX,
                                "0.00",
                                minimumValidFee.toString());

                productService.createProduct(product);

                final Product retrievedProduct = productService.findById(product.getId()).get();
                assertEquals(0, minimumValidFee.compareTo(retrievedProduct.getMonthlyFee()));
        }

        @Test
        @DisplayName("21: Should reject maximum invalid monthly fee boundary (0.10)")
        void shouldRejectMaximumInvalidMonthlyFeeBoundary() throws Exception {
                final Product product = createDefaultProduct(
                                "Boundary Test",
                                Brand.GMX,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final BigDecimal boundaryInvalidFee = new BigDecimal("0.10");

                assertThrows(
                                IllegalArgumentException.class,
                                () -> productService.updateMonthlyFee(product.getId(), boundaryInvalidFee));
        }

        @Test
        @DisplayName("22: Should handle large monthly fee values")
        void shouldHandleLargeMonthlyFeeValues() throws Exception {
                final Product product = createDefaultProduct(
                                "Large Fee Test",
                                Brand.WEB_DE,
                                "1.00",
                                "0.50");
                productService.createProduct(product);

                final BigDecimal largeFee = new BigDecimal("999999.99");
                productService.updateMonthlyFee(product.getId(), largeFee);

                final Product updatedProduct = productService.findById(product.getId()).get();
                assertEquals(0, largeFee.compareTo(updatedProduct.getMonthlyFee()));
        }

        @Test
        @DisplayName("23: Should handle zero setup fee")
        void shouldHandleZeroSetupFee() {
                final Product product = createDefaultProduct(
                                "Zero Setup Fee",
                                Brand.MAIL_COM,
                                "0.00",
                                "0.50");

                productService.createProduct(product);

                final Product retrievedProduct = productService.findById(product.getId()).get();
                assertEquals(0, BigDecimal.ZERO.compareTo(retrievedProduct.getSetupFee()));
        }
}
