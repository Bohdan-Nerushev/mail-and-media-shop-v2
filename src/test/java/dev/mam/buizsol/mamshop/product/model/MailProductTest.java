package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MailProduct Tests")
class MailProductTest {

        private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        private StandardMailProduct createDefaultStandardMailProduct(
                        String name,
                        Brand brand,
                        BigDecimal monthlyFee) {
                StandardMailProduct product = new StandardMailProduct(
                                name,
                                brand,
                                monthlyFee);
                validate(product);
                return product;
        }

        private PremiumMailProduct createDefaultPremiumMailProduct(
                        String name,
                        Brand brand,
                        BigDecimal monthlyFee) {
                PremiumMailProduct product = new PremiumMailProduct(
                                name,
                                brand,
                                monthlyFee);
                validate(product);
                return product;
        }

        private MailProduct createDefaultMailProduct(
                        String name,
                        Brand brand,
                        BigDecimal setupFee,
                        BigDecimal monthlyFee,
                        Long storageSize) {
                MailProduct product = new MailProduct(
                                name,
                                brand,
                                setupFee,
                                monthlyFee,
                                storageSize) {
                };
                validate(product);
                return product;
        }

        private void validate(final Product product) {
                Set<ConstraintViolation<Product>> violations = validator.validate(product);
                if (!violations.isEmpty()) {
                        throw new ProductValidationException("Validation failed");
                }
        }

        @Test
        @DisplayName("Verify StandardMailProduct fixed attributes (Setup Fee, Storage)")
        void shouldVerifyStandardMailProductFixedAttributes() {
                final StandardMailProduct product = createDefaultStandardMailProduct(
                                "Standard Mail Base",
                                Brand.GMX,
                                new BigDecimal("1.99"));

                assertNotNull(product.getId());
                assertEquals(new BigDecimal("4.99"), product.getSetupFee());
                assertEquals(4L, product.getStorageSize());
        }

        @Test
        @DisplayName("Verify PremiumMailProduct fixed attributes (Setup Fee, Storage)")
        void shouldVerifyPremiumMailProductFixedAttributes() {
                final PremiumMailProduct product = createDefaultPremiumMailProduct(
                                "Premium Mail Pro",
                                Brand.WEB_DE,
                                new BigDecimal("5.99"));

                assertNotNull(product.getId());
                assertEquals(new BigDecimal("9.99"), product.getSetupFee());
                assertEquals(8L, product.getStorageSize());
        }

        @Test
        @DisplayName("Verify MailProduct success at boundary minimum monthly fee (0.11€)")
        void shouldCreateMailProductWhenMonthlyFeeIsAtMinimumAllowed() {

                final BigDecimal minFee = new BigDecimal("0.11");

                final StandardMailProduct product = createDefaultStandardMailProduct(
                                "Budget Mail",
                                Brand.GMX,
                                minFee);

                assertEquals(minFee, product.getMonthlyFee());
        }

        @DisplayName("Verify MailProduct failure when monthly fee is below or at the limit (<= 0.10€)")
        @ParameterizedTest(name = "[{index}] Monthly fee {0} € should be invalid")
        @ValueSource(strings = { "0.10", "0.09", "0.00", "-0.01", "-100.00" })
        void shouldThrowExceptionWhenMailProductMonthlyFeeIsInvalid(
                        final String feeString) {

                final BigDecimal invalidFee = new BigDecimal(feeString);

                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultStandardMailProduct(
                                                "Invalid Fee Mail",
                                                Brand.WEB_DE,
                                                invalidFee));
        }

        @DisplayName("Verify MailProduct failure with null, empty or blank name")
        @ParameterizedTest(name = "[{index}] Name: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = { " ", "  ", "", "\t", "\n" })
        void shouldThrowExceptionWhenMailProductNameIsInvalid(
                        final String invalidName) {

                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultStandardMailProduct(
                                                invalidName,
                                                Brand.GMX,
                                                new BigDecimal("2.50")));
        }

        @Test
        @DisplayName("Verify MailProduct failure with null brand")
        void shouldThrowExceptionWhenMailProductBrandIsNull() {

                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultStandardMailProduct(
                                                "No Brand Mail",
                                                null,
                                                new BigDecimal("3.00")));
        }

        @Test
        @DisplayName("Verify MailProduct failure with null monthly fee")
        void shouldThrowExceptionWhenMailProductMonthlyFeeIsNull() {

                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultStandardMailProduct(
                                                "No Fee Mail",
                                                Brand.GMX,
                                                null));
        }

        @DisplayName("Verify MailProduct success with various valid and large monthly fees")
        @ParameterizedTest(name = "[{index}] Monthly fee {0} €")
        @CsvSource(value = {
                        "0.11",
                        "1",
                        "10.50",
                        "999.99",
                        "1000000"
        })
        void shouldCreateMailProductWhenMonthlyFeeIsValid(
                        final BigDecimal validFee) {

                final StandardMailProduct product = createDefaultStandardMailProduct(
                                "Dynamic Pricing Mail",
                                Brand.WEB_DE,
                                validFee);

                assertEquals(validFee, product.getMonthlyFee());
        }

        @DisplayName("Verify PremiumMailProduct failure when monthly fee is below or at the limit (<= 0.10€)")
        @ParameterizedTest(name = "[{index}] Monthly fee {0} € should be invalid")
        @ValueSource(strings = { "0.10", "0.09", "0.00", "-0.01", "-100.00" })
        void shouldThrowExceptionWhenPremiumMailProductMonthlyFeeIsInvalid(
                        final String feeString) {

                final BigDecimal invalidFee = new BigDecimal(feeString);

                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultPremiumMailProduct(
                                                "Premium Invalid Fee",
                                                Brand.WEB_DE,
                                                invalidFee));
        }

        @DisplayName("Verify PremiumMailProduct failure with null, empty or blank name")
        @ParameterizedTest(name = "[{index}] Name: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = { " ", "  ", "\t", "\n" })
        void shouldThrowExceptionWhenPremiumMailProductNameIsInvalid(
                        final String invalidName) {

                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultPremiumMailProduct(
                                                invalidName,
                                                Brand.WEB_DE,
                                                new BigDecimal("9.99")));
        }

        @DisplayName("Verify PremiumMailProduct success with various valid monthly fees")
        @ParameterizedTest(name = "[{index}] Monthly fee {0} €")
        @CsvSource(value = {
                        "0.11",
                        "1",
                        "10.50",
                        "99.99",
                        "1000",
                        "10000.50"
        })
        void shouldCreatePremiumMailProductWhenMonthlyFeeIsValid(
                        final BigDecimal validFee) {

                final PremiumMailProduct product = createDefaultPremiumMailProduct(
                                "Premium High Tier",
                                Brand.WEB_DE,
                                validFee);

                assertEquals(validFee, product.getMonthlyFee());
        }

        @DisplayName("Verify StandardMailProduct creation with all available brands")
        @ParameterizedTest(name = "[{index}] Brand: {0}")
        @ValueSource(strings = { "GMX", "WEB_DE" })
        void shouldCreateStandardMailProductForAllBrands(String brandName) {
                Brand brand = Brand.valueOf(brandName);
                final StandardMailProduct product = createDefaultStandardMailProduct(
                                "Brand Test Mail",
                                brand,
                                new BigDecimal("4.50"));

                assertEquals(brand, product.getBrand());
        }

        @Test
        @DisplayName("Verify PremiumMailProduct failure with null brand")
        void shouldThrowExceptionWhenPremiumMailProductBrandIsNull() {

                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultPremiumMailProduct(
                                                "Premium Null Brand",
                                                null,
                                                new BigDecimal("12.00")));
        }

        @Test
        @DisplayName("Verify PremiumMailProduct success at boundary minimum monthly fee (0.11€)")
        void shouldCreatePremiumMailProductWhenMonthlyFeeIsAtMinimumAllowed() {

                final BigDecimal minFee = new BigDecimal("0.11");

                final PremiumMailProduct product = createDefaultPremiumMailProduct(
                                "Premium Budget",
                                Brand.WEB_DE,
                                minFee);

                assertEquals(minFee, product.getMonthlyFee());
        }

        @Test
        @DisplayName("Negative: Failure with invalid storage size (< 1GB)")
        void shouldThrowExceptionWhenStorageSizeIsInvalid() {
                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultMailProduct("Small Storage", Brand.GMX, BigDecimal.ZERO,
                                                BigDecimal.ONE, 0L));
        }

        @Test
        @DisplayName("Negative: Failure with invalid storage size (< 1GB)")
        void shouldThrowProductValidationExceptionWhenStorageSizeIsInvalid() {
                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultMailProduct("Small Storage", Brand.GMX, BigDecimal.ZERO,
                                                BigDecimal.ONE, 0L));
                assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultMailProduct("Small Storage", Brand.GMX, BigDecimal.ZERO,
                                                BigDecimal.ONE, null));
        }
}
