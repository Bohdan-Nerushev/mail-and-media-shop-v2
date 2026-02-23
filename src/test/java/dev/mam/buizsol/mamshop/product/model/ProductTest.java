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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Product Tests")
class ProductTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Product createDefaultProduct(
            final String name,
            final Brand brand,
            final BigDecimal setupFee,
            final BigDecimal monthlyFee) {
        Product p = new Product(name, brand, setupFee, monthlyFee) {
        };
        Set<ConstraintViolation<Product>> violations = validator.validate(p);
        if (violations.isEmpty()) {
            return p;
        }
        String message = violations.size() == 1
                ? violations.iterator().next().getMessage()
                : "Validation failed";
        throw new ProductValidationException(message);
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
        assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct(invalidName, Brand.GMX, BigDecimal.ZERO, BigDecimal.ONE));
    }

    @Test
    @DisplayName("Negative: Failure with null brand")
    void shouldThrowExceptionWhenBrandIsNull() {
        assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct("P", null, BigDecimal.ZERO, BigDecimal.ONE));
    }

    @DisplayName("Negative: Failure with null fees")
    @ParameterizedTest(name = "[{index}] Setup: {0}, Monthly: {1}")
    @CsvSource({
            ", 1.00",
            "0.00, "
    })
    void shouldThrowExceptionWhenFeesAreNull(BigDecimal setupFee, BigDecimal monthlyFee) {
        assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct("P", Brand.GMX, setupFee, monthlyFee));
    }

    @Test
    @DisplayName("Negative: Failure with negative setup fee")
    void shouldThrowExceptionWhenSetupFeeIsNegative() {
        BigDecimal negativeFee = new BigDecimal("-0.01");

        assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct("P", Brand.GMX, negativeFee, BigDecimal.ONE));
    }

    @DisplayName("Negative: Failure with monthly fee <= 0.10")
    @ParameterizedTest(name = "[{index}] Monthly fee: {0}")
    @ValueSource(strings = { "0.10", "0.09", "0.00", "-1.00" })
    void shouldThrowExceptionWhenMonthlyFeeIsInvalid(String fee) {
        BigDecimal invalidMonthlyFee = new BigDecimal(fee);

        assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct("P", Brand.GMX, BigDecimal.ZERO, invalidMonthlyFee));
    }

    @Test
    @DisplayName("Boundary: Success with maximum allowed monthly fee (0.11)")
    void shouldSucceedWhenMonthlyFeeIsAtMinimumAllowed() {
        BigDecimal minFee = new BigDecimal("0.11");
        Product p = createDefaultProduct("P", Brand.GMX, BigDecimal.ZERO, minFee);

        assertEquals(minFee, p.getMonthlyFee());
    }

    @Test
    @DisplayName("Negative: Failure with too long name (> 100 characters)")
    void shouldThrowExceptionWhenProductNameIsTooLong() {
        String longName = "A".repeat(101);

        ProductValidationException exception = assertThrows(
                ProductValidationException.class,
                () -> createDefaultProduct(longName, Brand.GMX, BigDecimal.ZERO, BigDecimal.ONE));

        assertEquals("Product name must not exceed 100 characters", exception.getMessage());
    }
}
