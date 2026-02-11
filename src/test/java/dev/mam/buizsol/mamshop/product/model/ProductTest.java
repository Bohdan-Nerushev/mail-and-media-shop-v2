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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Product Tests")
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
    @DisplayName("Success: Create Product and verify fields and ID uniqueness")
    void shouldCreateProductAndVerifyFieldsWhenDataIsValid() {
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
    @DisplayName("Success: Verify mass ID uniqueness (1000 instances)")
    void shouldGenerateUniqueIdsWhenMassCreatingProducts() {
        int count = 1000;
        Set<UUID> verifyIdsSuccess = new HashSet<>();

        for (int i = 0; i < count; i++) {
            Product p = createDefaultProduct("P" + i, Brand.GMX, BigDecimal.ZERO, BigDecimal.ONE);
            verifyIdsSuccess.add(p.getId());
        }

        assertEquals(count, verifyIdsSuccess.size(), "All generated IDs must be unique");
    }

    @DisplayName("Negative: Failure with invalid names (null, empty, blank)")
    @ParameterizedTest(name = "[{index}] Name: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    void shouldThrowExceptionWhenProductNameIsInvalid(String invalidName) {
        ProductValidationException exception = assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct(invalidName, Brand.GMX, BigDecimal.ZERO, BigDecimal.ONE));

        assertEquals("Product name must not be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Negative: Failure with null brand")
    void shouldThrowExceptionWhenBrandIsNull() {
        ProductValidationException exception = assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct("P", null, BigDecimal.ZERO, BigDecimal.ONE));

        assertEquals("Brand must not be null", exception.getMessage());
    }

    @DisplayName("Negative: Failure with null fees")
    @ParameterizedTest(name = "[{index}] Setup: {0}, Monthly: {1}")
    @CsvSource({
            ", 1.00",
            "0.00, "
    })
    void shouldThrowExceptionWhenFeesAreNull(BigDecimal setupFee, BigDecimal monthlyFee) {
        String expectedField = setupFee == null ? "Setup fee" : "Monthly fee";

        ProductValidationException exception = assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct("P", Brand.GMX, setupFee, monthlyFee));

        assertEquals(expectedField + " must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Negative: Failure with negative setup fee")
    void shouldThrowExceptionWhenSetupFeeIsNegative() {
        BigDecimal negativeFee = new BigDecimal("-0.01");

        ProductValidationException exception = assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct("P", Brand.GMX, negativeFee, BigDecimal.ONE));

        assertEquals("Setup fee must not be negative", exception.getMessage());
    }

    @DisplayName("Negative: Failure with monthly fee <= 0.10")
    @ParameterizedTest(name = "[{index}] Monthly fee: {0}")
    @ValueSource(strings = { "0.10", "0.09", "0.00", "-1.00" })
    void shouldThrowExceptionWhenMonthlyFeeIsInvalid(String fee) {
        BigDecimal invalidMonthlyFee = new BigDecimal(fee);

        ProductValidationException exception = assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct("P", Brand.GMX, BigDecimal.ZERO, invalidMonthlyFee));

        assertEquals("Monthly fee must be greater than 0.10 €", exception.getMessage());
    }

    @Test
    @DisplayName("Boundary: Success with minimum allowed monthly fee (0.11)")
    void shouldSucceedWhenMonthlyFeeIsAtMinimumAllowed() {
        BigDecimal minFee = new BigDecimal("0.11");
        Product p = createDefaultProduct("P", Brand.GMX, BigDecimal.ZERO, minFee);

        assertEquals(minFee, p.getMonthlyFee());
    }
}
