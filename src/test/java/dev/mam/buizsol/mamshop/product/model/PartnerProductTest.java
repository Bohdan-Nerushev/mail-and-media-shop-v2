package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
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

class PartnerProductTest {

    private PartnerProduct createPartnerProduct(
            final String name,
            final Brand brand,
            final BigDecimal setupFee,
            final BigDecimal monthlyFee) {
        return new PartnerProduct(name, brand, setupFee, monthlyFee);
    }

    @ParameterizedTest(name = "[{index}] Name: {0}, Brand: {1}")
    @CsvSource({
            "Cloud Storage, GMX",
            "Video Streaming, WEB_DE",
            "Security Pack, GMX"
    })
    @DisplayName("1. Success: Create PartnerProduct with valid diverse data")
    void test1_CreatePartnerProduct_Success(String name, String brandName) {
        Brand brand = Brand.valueOf(brandName);
        BigDecimal setupFee = new BigDecimal("9.99");
        BigDecimal monthlyFee = new BigDecimal("4.99");

        PartnerProduct product = createPartnerProduct(name, brand, setupFee, monthlyFee);

        assertNotNull(product.getId());
        assertEquals(name, product.getName());
        assertEquals(brand, product.getBrand());
        assertEquals(setupFee, product.getSetupFee());
        assertEquals(monthlyFee, product.getMonthlyFee());
    }

    @Test
    @DisplayName("2. Boundary: Success with minimum allowed monthly fee (0.11 €)")
    void test2_CreatePartnerProduct_Boundary_MinMonthlyFee_Success() {
        BigDecimal minFee = new BigDecimal("0.11");
        PartnerProduct product = createPartnerProduct(
                "Budget Cloud",
                Brand.GMX,
                BigDecimal.ZERO,
                minFee);

        assertEquals(minFee, product.getMonthlyFee());
    }

    @ParameterizedTest(name = "[{index}] Monthly fee {0} €")
    @ValueSource(strings = { "0.10", "0.09", "0.00", "-0.01", "-10.00" })
    @DisplayName("3. Boundary/Negative: Failure with monthly fee 0.10 € or less")
    void test3_CreatePartnerProduct_InvalidMonthlyFee_ThrowsException(String fee) {
        BigDecimal invalidFee = new BigDecimal(fee);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createPartnerProduct("Invalid Fee Product", Brand.WEB_DE, BigDecimal.ONE, invalidFee));

        assertEquals("Monthly fee must be greater than 0.10 €", exception.getMessage());
    }

    @ParameterizedTest(name = "[{index}] Invalid Name: \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    @DisplayName("4. Negative: Failure with null, empty or blank name")
    void test4_CreatePartnerProduct_InvalidName_ThrowsException(String name) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createPartnerProduct(name, Brand.GMX, BigDecimal.ZERO, new BigDecimal("1.00")));

        assertEquals("Product name must not be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("5. Negative: Failure with null brand")
    void test5_CreatePartnerProduct_NullBrand_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createPartnerProduct("No Brand", null, BigDecimal.ZERO, new BigDecimal("1.00")));

        assertEquals("Brand must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("6. Negative: Failure with null setup fee")
    void test6_CreatePartnerProduct_NullSetupFee_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createPartnerProduct("No Setup Fee", Brand.WEB_DE, null, new BigDecimal("1.00")));

        assertEquals("Setup fee must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("7. Negative: Failure with null monthly fee")
    void test7_CreatePartnerProduct_NullMonthlyFee_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createPartnerProduct("No Monthly Fee", Brand.GMX, BigDecimal.ZERO, null));

        assertEquals("Monthly fee must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("8. Negative: Failure with negative setup fee")
    void test8_CreatePartnerProduct_NegativeSetupFee_ThrowsException() {
        BigDecimal negativeFee = new BigDecimal("-1.00");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createPartnerProduct("Bad Setup Fee", Brand.GMX, negativeFee, new BigDecimal("1.00")));

        assertEquals("Setup fee must not be negative", exception.getMessage());
    }
}
