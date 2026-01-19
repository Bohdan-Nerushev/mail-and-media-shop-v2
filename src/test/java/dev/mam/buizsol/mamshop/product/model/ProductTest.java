package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    private Product createDefaultProduct(
            final String name,
            final Brand brand,
            final BigDecimal setupFee,
            final BigDecimal monthlyFee) {
        return new Product(name, brand, setupFee, monthlyFee) {
        };
    }

    @Test
    @DisplayName("1. Success: Create Product and verify fields and ID uniqueness")
    void test1_CreateProduct_Success() {
        Product p1 = createDefaultProduct("P1", Brand.GMX, BigDecimal.ZERO, BigDecimal.ONE);
        Product p2 = createDefaultProduct("P2", Brand.WEB_DE, BigDecimal.TEN, new BigDecimal("5.50"));

        assertNotNull(p1.getId());
        assertNotNull(p2.getId());
        assertNotEquals(p1.getId(), p2.getId());
        assertEquals("P1", p1.getName());
        assertEquals(Brand.GMX, p1.getBrand());
        assertEquals(BigDecimal.ZERO, p1.getSetupFee());
        assertEquals(BigDecimal.ONE, p1.getMonthlyFee());
    }

    @Test
    @DisplayName("2. Success: Verify mass ID uniqueness (1000 instances)")
    void test2_MassProductID_Uniqueness() {
        int count = 1000;
        Set<UUID> ids = new HashSet<>();

        for (int i = 0; i < count; i++) {
            Product p = createDefaultProduct("P" + i, Brand.GMX, BigDecimal.ZERO, BigDecimal.ONE);
            ids.add(p.getId());
        }

        assertEquals(count, ids.size(), "All generated IDs must be unique");
    }

    @ParameterizedTest(name = "[{index}] Name: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    @DisplayName("3. Negative: Failure with invalid names (null, empty, blank)")
    void test3_CreateProduct_InvalidName_ThrowsException(String invalidName) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createDefaultProduct(invalidName, Brand.GMX, BigDecimal.ZERO, BigDecimal.ONE));

        assertEquals("Product name must not be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("4. Negative: Failure with null brand")
    void test4_CreateProduct_NullBrand_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createDefaultProduct("P", null, BigDecimal.ZERO, BigDecimal.ONE));

        assertEquals("Brand must not be null", exception.getMessage());
    }

    @ParameterizedTest(name = "[{index}] Setup: {0}, Monthly: {1}")
    @CsvSource({
            ", 1.00",
            "0.00, "
    })
    @DisplayName("5. Negative: Failure with null fees")
    void test5_CreateProduct_NullFees_ThrowsException(BigDecimal setupFee, BigDecimal monthlyFee) {
        String expectedField = setupFee == null ? "Setup fee" : "Monthly fee";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createDefaultProduct("P", Brand.GMX, setupFee, monthlyFee));

        assertEquals(expectedField + " must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("6. Negative: Failure with negative setup fee")
    void test6_CreateProduct_NegativeSetupFee_ThrowsException() {
        BigDecimal negativeFee = new BigDecimal("-0.01");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createDefaultProduct("P", Brand.GMX, negativeFee, BigDecimal.ONE));

        assertEquals("Setup fee must not be negative", exception.getMessage());
    }

    @ParameterizedTest(name = "[{index}] Monthly fee: {0}")
    @ValueSource(strings = { "0.10", "0.09", "0.00", "-1.00" })
    @DisplayName("7. Negative: Failure with monthly fee <= 0.10")
    void test7_CreateProduct_InvalidMonthlyFee_ThrowsException(String fee) {
        BigDecimal invalidMonthlyFee = new BigDecimal(fee);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createDefaultProduct("P", Brand.GMX, BigDecimal.ZERO, invalidMonthlyFee));

        assertEquals("Monthly fee must be greater than 0.10 €", exception.getMessage());
    }

    @Test
    @DisplayName("8. Boundary: Success with minimum allowed monthly fee (0.11)")
    void test8_CreateProduct_MinMonthlyFee_Success() {
        BigDecimal minFee = new BigDecimal("0.11");
        Product p = createDefaultProduct("P", Brand.GMX, BigDecimal.ZERO, minFee);

        assertEquals(minFee, p.getMonthlyFee());
    }
}
