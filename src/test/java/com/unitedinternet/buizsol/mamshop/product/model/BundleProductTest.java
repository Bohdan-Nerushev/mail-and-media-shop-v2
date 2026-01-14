package com.unitedinternet.buizsol.mamshop.product.model;

import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BundleProductTest {

    @ParameterizedTest(name = "[{index}] Brand: {0}, Mail: {1}, Partner: {2}")
    @MethodSource("provideValidBundleComponents")
    @DisplayName("1. Success: Create BundleProduct and verify summation logic for different brands")
    void test1_CreateBundleProduct_Success(Brand brand, MailProduct mail, PartnerProduct partner) {
        BundleProduct bundle = new BundleProduct(mail, partner);

        assertNotNull(bundle.getId());
        assertEquals(brand, bundle.getBrand());
        assertEquals("Bundle: " + mail.getName() + " " + partner.getName(), bundle.getName());
        assertEquals(mail.getSetupFee().add(partner.getSetupFee()), bundle.getSetupFee());
        assertEquals(mail.getMonthlyFee().add(partner.getMonthlyFee()), bundle.getMonthlyFee());
    }

    @Test
    @DisplayName("2. Negative: Failure when creating bundle with mismatched brands")
    void test2_CreateBundleProduct_MismatchedBrands_ThrowsException() {
        MailProduct mail = new StandardMailProduct("Mail", Brand.GMX, new BigDecimal("1.00"));
        PartnerProduct partner = new PartnerProduct("Cloud", Brand.WEB_DE, BigDecimal.ZERO, new BigDecimal("2.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new BundleProduct(mail, partner));

        assertEquals("Brands must match for bundle product", exception.getMessage());
    }

    @ParameterizedTest(name = "[{index}] Mail: {0}, Partner: {1}")
    @MethodSource("provideNullComponents")
    @DisplayName("3. Negative: Failure with null components")
    void test3_CreateBundleProduct_NullComponents_ThrowsException(MailProduct mail, PartnerProduct partner) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new BundleProduct(mail, partner));

        assertEquals("Mail and Partner products must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("4. Boundary: Success with large fees summation")
    void test4_CreateBundleProduct_LargeFees_Success() {
        BigDecimal largeFee = new BigDecimal("1000000.00");
        MailProduct mail = new PremiumMailProduct("Rich Mail", Brand.GMX, largeFee);
        PartnerProduct partner = new PartnerProduct("Rich Cloud", Brand.GMX, largeFee, largeFee);

        BundleProduct bundle = new BundleProduct(mail, partner);

        assertEquals(new BigDecimal("9.99").add(largeFee), bundle.getSetupFee());
        assertEquals(largeFee.add(largeFee), bundle.getMonthlyFee());
    }

    @Test
    @DisplayName("5. Polymorphism: Verify calls via Product base class")
    void test5_BundleProduct_PolymorphicCalls() {
        MailProduct mail = new StandardMailProduct("Base Mail", Brand.WEB_DE, new BigDecimal("1.50"));
        PartnerProduct partner = new PartnerProduct("Base Cloud", Brand.WEB_DE, new BigDecimal("10.00"),
                new BigDecimal("3.50"));

        Product product = new BundleProduct(mail, partner);

        assertEquals(new BigDecimal("14.99"), product.getSetupFee());
        assertEquals(new BigDecimal("5.00"), product.getMonthlyFee());
    }

    private static Stream<Arguments> provideValidBundleComponents() {
        return Stream.of(
                Arguments.of(
                        Brand.GMX,
                        new StandardMailProduct("S-Mail", Brand.GMX, new BigDecimal("1.00")),
                        new PartnerProduct("P-Cloud", Brand.GMX, new BigDecimal("5.00"), new BigDecimal("2.00"))),
                Arguments.of(
                        Brand.WEB_DE,
                        new PremiumMailProduct("P-Mail", Brand.WEB_DE, new BigDecimal("10.00")),
                        new PartnerProduct("P-Music", Brand.WEB_DE, new BigDecimal("15.00"), new BigDecimal("5.00"))));
    }

    private static Stream<Arguments> provideNullComponents() {
        return Stream.of(
                Arguments.of(null, new PartnerProduct("P", Brand.GMX, BigDecimal.ZERO, new BigDecimal("1.00"))),
                Arguments.of(new StandardMailProduct("M", Brand.GMX, new BigDecimal("1.00")), null),
                Arguments.of(null, null));
    }
}
