package com.unitedinternet.buizsol.mamshop.product.model;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MailProductTest {

        private StandardMailProduct createDefaultStandardMailProduct(
                        String name,
                        Brand brand,
                        BigDecimal monthlyFee) {
                return new StandardMailProduct(
                                name,
                                brand,
                                monthlyFee);
        }

        private PremiumMailProduct createDefaultPremiumMailProduct(
                        String name,
                        Brand brand,
                        BigDecimal monthlyFee) {
                return new PremiumMailProduct(
                                name,
                                brand,
                                monthlyFee);
        }

        private MailProduct createDefaultMailProduct(
                        String name,
                        Brand brand,
                        BigDecimal setupFee,
                        BigDecimal monthlyFee,
                        Long storageSize) {
                return new MailProduct(
                                name,
                                brand,
                                setupFee,
                                monthlyFee,
                                storageSize) {
                };
        }

        @Test
        @DisplayName("1. Verify StandardMailProduct fixed attributes (Setup Fee, Storage)")
        void test1_CreateStandardMailProduct_VerifyFixedFeesAndStorage() {
                final StandardMailProduct product = createDefaultStandardMailProduct(
                                "Standard Mail Base",
                                Brand.GMX,
                                new BigDecimal("1.99"));

                assertNotNull(product.getId());
                assertEquals(new BigDecimal("4.99"), product.getSetupFee());
                assertEquals(4L, product.getStorageSize());
        }

        @Test
        @DisplayName("2. Verify PremiumMailProduct fixed attributes (Setup Fee, Storage)")
        void test2_CreatePremiumMailProduct_VerifyFixedFeesAndStorage() {
                final PremiumMailProduct product = createDefaultPremiumMailProduct(
                                "Premium Mail Pro",
                                Brand.WEB_DE,
                                new BigDecimal("5.99"));

                assertNotNull(product.getId());
                assertEquals(new BigDecimal("9.99"), product.getSetupFee());
                assertEquals(8L, product.getStorageSize());
        }

        @Test
        @DisplayName("3. Verify MailProduct success at boundary minimum monthly fee (0.11€)")
        void test3_CreateMailProduct_Success_BoundaryMinimumMonthlyFee() {

                final BigDecimal minFee = new BigDecimal("0.11");

                final StandardMailProduct product = createDefaultStandardMailProduct(
                                "Budget Mail",
                                Brand.GMX,
                                minFee);

                assertEquals(minFee, product.getMonthlyFee());
        }

        @ParameterizedTest(name = "[{index}] Monthly fee {0} € should be invalid")
        @ValueSource(strings = { "0.10", "0.09", "0.00", "-0.01", "-100.00" })
        @DisplayName("4. Verify MailProduct failure when monthly fee is below or at the limit (<= 0.10€)")
        void test4_CreateMailProduct_ThrowsException_MonthlyFeeBelowOrAtLimit(
                        final String feeString) {

                final BigDecimal invalidFee = new BigDecimal(feeString);

                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createDefaultStandardMailProduct(
                                                "Invalid Fee Mail",
                                                Brand.WEB_DE,
                                                invalidFee));

                assertEquals("Monthly fee must be greater than 0.10 €", exception.getMessage());
        }

        @ParameterizedTest(name = "[{index}] Name: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = { " ", "  ", "\t", "\n" })
        @DisplayName("5. Verify MailProduct failure with null, empty or blank name")
        void test5_CreateMailProduct_ThrowsException_InvalidName(
                        final String invalidName) {

                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createDefaultStandardMailProduct(
                                                invalidName,
                                                Brand.GMX,
                                                new BigDecimal("2.50")));

                assertEquals("Product name must not be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("6. Verify MailProduct failure with null brand")
        void test6_CreateMailProduct_ThrowsException_NullBrand() {

                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createDefaultStandardMailProduct(
                                                "No Brand Mail",
                                                null,
                                                new BigDecimal("3.00")));

                assertEquals("Brand must not be null", exception.getMessage());
        }

        @Test
        @DisplayName("7. Verify MailProduct failure with null monthly fee")
        void test7_CreateMailProduct_ThrowsException_NullMonthlyFee() {

                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createDefaultStandardMailProduct(
                                                "No Fee Mail",
                                                Brand.GMX,
                                                null));

                assertEquals("Monthly fee must not be null", exception.getMessage());
        }

        @ParameterizedTest(name = "[{index}] Monthly fee {0} €")
        @CsvSource(value = {
                        "0.11",
                        "1",
                        "10.50",
                        "999.99",
                        "1000000"
        })
        @DisplayName("8. Verify MailProduct success with various valid and large monthly fees")
        void test8_CreateMailProduct_Success_VariousValidMonthlyFees(
                        final BigDecimal validFee) {

                final StandardMailProduct product = createDefaultStandardMailProduct(
                                "Dynamic Pricing Mail",
                                Brand.WEB_DE,
                                validFee);

                assertEquals(validFee, product.getMonthlyFee());
        }

        @ParameterizedTest(name = "[{index}] Monthly fee {0} € should be invalid")
        @ValueSource(strings = { "0.10", "0.09", "0.00", "-0.01", "-100.00" })
        @DisplayName("9. Verify PremiumMailProduct failure when monthly fee is below or at the limit (<= 0.10€)")
        void test9_CreatePremiumMailProduct_ThrowsException_MonthlyFeeBelowOrAtLimit(
                        final String feeString) {

                final BigDecimal invalidFee = new BigDecimal(feeString);

                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createDefaultPremiumMailProduct(
                                                "Premium Invalid Fee",
                                                Brand.WEB_DE,
                                                invalidFee));

                assertEquals("Monthly fee must be greater than 0.10 €", exception.getMessage());
        }

        @ParameterizedTest(name = "[{index}] Name: ''{0}''")
        @NullAndEmptySource
        @ValueSource(strings = { " ", "  ", "\t", "\n" })
        @DisplayName("10. Verify PremiumMailProduct failure with null, empty or blank name")
        void test10_CreatePremiumMailProduct_ThrowsException_InvalidName(
                        final String invalidName) {

                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createDefaultPremiumMailProduct(
                                                invalidName,
                                                Brand.WEB_DE,
                                                new BigDecimal("9.99")));

                assertEquals("Product name must not be null or empty", exception.getMessage());
        }

        @ParameterizedTest(name = "[{index}] Monthly fee {0} €")
        @CsvSource(value = {
                        "0.11",
                        "1",
                        "10.50",
                        "99.99",
                        "1000",
                        "10000.50"
        })
        @DisplayName("13. Verify PremiumMailProduct success with various valid monthly fees")
        void test13_CreatePremiumMailProduct_Success_VariousValidMonthlyFees(
                        final BigDecimal validFee) {

                final PremiumMailProduct product = createDefaultPremiumMailProduct(
                                "Premium High Tier",
                                Brand.WEB_DE,
                                validFee);

                assertEquals(validFee, product.getMonthlyFee());
        }

        @ParameterizedTest(name = "[{index}] Brand: {0}")
        @ValueSource(strings = { "GMX", "WEB_DE" })
        @DisplayName("14. Verify StandardMailProduct creation with all available brands")
        void test14_CreateStandardMailProduct_AllBrands(String brandName) {
                Brand brand = Brand.valueOf(brandName);
                final StandardMailProduct product = createDefaultStandardMailProduct(
                                "Brand Test Mail",
                                brand,
                                new BigDecimal("4.50"));

                assertEquals(brand, product.getBrand());
        }

        @Test
        @DisplayName("11. Verify PremiumMailProduct failure with null brand")
        void test11_CreatePremiumMailProduct_ThrowsException_NullBrand() {

                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createDefaultPremiumMailProduct(
                                                "Premium Null Brand",
                                                null,
                                                new BigDecimal("12.00")));

                assertEquals("Brand must not be null", exception.getMessage());
        }

        @Test
        @DisplayName("12. Verify PremiumMailProduct success at boundary minimum monthly fee (0.11€)")
        void test12_CreatePremiumMailProduct_Success_BoundaryMinimumMonthlyFee() {

                final BigDecimal minFee = new BigDecimal("0.11");

                final PremiumMailProduct product = createDefaultPremiumMailProduct(
                                "Premium Budget",
                                Brand.WEB_DE,
                                minFee);

                assertEquals(minFee, product.getMonthlyFee());
        }

        @Test
        @DisplayName("15. Negative: Failure with invalid storage size (< 1GB)")
        void test15_CreateMailProduct_InvalidStorageSize() {
                final IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createDefaultMailProduct("Small Storage", Brand.GMX, BigDecimal.ZERO,
                                                BigDecimal.ONE, 0L));

                assertEquals("Storage size must be at least 1GB", exception.getMessage());
        }
}
