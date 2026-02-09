package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BundleProductTest {

        private StandardMailProduct createDefaultStandardMailProduct(
                        final String name,
                        final Brand brand,
                        final BigDecimal monthlyFee) {
                return new StandardMailProduct(name, brand, monthlyFee);
        }

        private PremiumMailProduct createDefaultPremiumMailProduct(
                        final String name,
                        final Brand brand,
                        final BigDecimal monthlyFee) {
                return new PremiumMailProduct(name, brand, monthlyFee);
        }

        private PartnerProduct createDefaultPartnerProduct(
                        final String name,
                        final Brand brand,
                        final BigDecimal setupFee,
                        final BigDecimal monthlyFee) {
                return new PartnerProduct(name, brand, setupFee, monthlyFee);
        }

        private BundleProduct createDefaultBundleProduct(
                        final MailProduct mail,
                        final PartnerProduct partner) {
                return new BundleProduct(mail, partner);
        }

        @ParameterizedTest(name = "[{index}] Brand: {0}, Mail: {1}, Partner: {3}")
        @CsvSource({
                        "GMX, S-Mail, 1.00, P-Cloud, 5.00, 2.00, 9.99, 3.00",
                        "WEB_DE, P-Mail, 10.00, P-Music, 15.00, 5.00, 24.99, 15.00"
        })
        @DisplayName("1. Success: Create BundleProduct and verify summation logic for different brands")
        void test1_CreateBundleProduct_Success(
                        final Brand brand,
                        final String mailName,
                        final BigDecimal mailMonthly,
                        final String partnerName,
                        final BigDecimal partnerSetup,
                        final BigDecimal partnerMonthly,
                        final BigDecimal expectedSetup,
                        final BigDecimal expectedMonthly) {

                final MailProduct mail = mailName.startsWith("S")
                                ? createDefaultStandardMailProduct(mailName, brand, mailMonthly)
                                : createDefaultPremiumMailProduct(mailName, brand, mailMonthly);

                final PartnerProduct partner = createDefaultPartnerProduct(partnerName, brand, partnerSetup,
                                partnerMonthly);
                final BundleProduct bundle = createDefaultBundleProduct(mail, partner);

                assertNotNull(bundle.getId());
                assertEquals(brand, bundle.getBrand());
                assertEquals("Bundle: " + mail.getName() + " " + partner.getName(), bundle.getName());
                assertEquals(expectedSetup, bundle.getSetupFee());
                assertEquals(expectedMonthly, bundle.getMonthlyFee());
        }

        @Test
        @DisplayName("2. Negative: Failure when creating bundle with mismatched brands")
        void test2_CreateBundleProduct_MismatchedBrands_ThrowsException() {
                StandardMailProduct mail = createDefaultStandardMailProduct("Mail", Brand.GMX, new BigDecimal("1.00"));
                PartnerProduct partner = createDefaultPartnerProduct("Cloud", Brand.WEB_DE, BigDecimal.ZERO,
                                new BigDecimal("2.00"));

                ProductValidationException exception = assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultBundleProduct(mail, partner));

                assertEquals("Brands must match for bundle product", exception.getMessage());
        }

        @Test
        @DisplayName("3. Negative: Failure with null MailProduct")
        void test3_CreateBundleProduct_NullMail_ThrowsException() {
                final PartnerProduct partner = createDefaultPartnerProduct("P", Brand.GMX, BigDecimal.ZERO,
                                BigDecimal.ONE);

                final ProductValidationException exception = assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultBundleProduct(null, partner));

                assertEquals("Mail and Partner products must not be null", exception.getMessage());
        }

        @Test
        @DisplayName("4. Negative: Failure with null PartnerProduct")
        void test4_CreateBundleProduct_NullPartner_ThrowsException() {
                final MailProduct mail = createDefaultStandardMailProduct("M", Brand.GMX, BigDecimal.ONE);

                final ProductValidationException exception = assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultBundleProduct(mail, null));

                assertEquals("Mail and Partner products must not be null", exception.getMessage());
        }

        @Test
        @DisplayName("5. Negative: Failure with both components null")
        void test5_CreateBundleProduct_BothNull_ThrowsException() {
                final ProductValidationException exception = assertThrows(
                                ProductValidationException.class,
                                () -> createDefaultBundleProduct(null, null));

                assertEquals("Mail and Partner products must not be null", exception.getMessage());
        }

        @Test
        @DisplayName("6. Boundary: Success with large fees summation")
        void test6_CreateBundleProduct_LargeFees_Success() {
                BigDecimal largeFee = new BigDecimal("1000000.00");
                PremiumMailProduct mail = createDefaultPremiumMailProduct("Rich Mail", Brand.GMX, largeFee);
                PartnerProduct partner = createDefaultPartnerProduct("Rich Cloud", Brand.GMX, largeFee, largeFee);

                BundleProduct bundle = createDefaultBundleProduct(mail, partner);

                assertEquals(new BigDecimal("9.99").add(largeFee), bundle.getSetupFee());
                assertEquals(largeFee.add(largeFee), bundle.getMonthlyFee());
        }

        @Test
        @DisplayName("7. Polymorphism: Verify calls via Product base class")
        void test7_BundleProduct_PolymorphicCalls() {
                MailProduct mail = createDefaultStandardMailProduct("Base Mail", Brand.WEB_DE, new BigDecimal("1.50"));
                PartnerProduct partner = createDefaultPartnerProduct("Base Cloud", Brand.WEB_DE,
                                new BigDecimal("10.00"),
                                new BigDecimal("3.50"));

                Product product = createDefaultBundleProduct(mail, partner);

                assertEquals(mail.getSetupFee().add(partner.getSetupFee()), product.getSetupFee());
                assertEquals(mail.getMonthlyFee().add(partner.getMonthlyFee()), product.getMonthlyFee());
        }

        @Test
        @DisplayName("8. Success: Verify getters for components")
        void test8_BundleProduct_VerifyGetters() {
                StandardMailProduct mail = createDefaultStandardMailProduct("Mail", Brand.GMX, new BigDecimal("1.00"));
                PartnerProduct partner = createDefaultPartnerProduct("Partner", Brand.GMX, BigDecimal.ZERO,
                                new BigDecimal("1.00"));

                BundleProduct bundle = createDefaultBundleProduct(mail, partner);

                assertEquals(mail, bundle.getMailProduct());
                assertEquals(partner, bundle.getPartnerProduct());
        }

        @Test
        @DisplayName("9. Negative: setMonthlyFee throws UnsupportedOperationException")
        void test9_SetMonthlyFee_ThrowsException() {
                StandardMailProduct mail = createDefaultStandardMailProduct("Mail", Brand.GMX, new BigDecimal("1.00"));
                PartnerProduct partner = createDefaultPartnerProduct("Partner", Brand.GMX, BigDecimal.ZERO,
                                new BigDecimal("1.00"));
                BundleProduct bundle = createDefaultBundleProduct(mail, partner);

                assertThrows(UnsupportedOperationException.class,
                                () -> bundle.setMonthlyFee(new BigDecimal("5.00")));
        }

        @Test
        @DisplayName("10. Dynamic: Bundle fee updates when components change")
        void test10_DynamicFeeCalculation() {
                BigDecimal initialFee = new BigDecimal("1.00");
                StandardMailProduct mail = createDefaultStandardMailProduct("Mail", Brand.GMX, initialFee);
                PartnerProduct partner = createDefaultPartnerProduct("Partner", Brand.GMX, BigDecimal.ZERO,
                                new BigDecimal("1.00"));
                BundleProduct bundle = createDefaultBundleProduct(mail, partner);

                assertEquals(new BigDecimal("2.00"), bundle.getMonthlyFee());

                BigDecimal newFee = new BigDecimal("5.00");
                mail.setMonthlyFee(newFee);

                assertEquals(newFee.add(partner.getMonthlyFee()), bundle.getMonthlyFee(),
                                "Bundle fee should reflect component change");
        }
}
