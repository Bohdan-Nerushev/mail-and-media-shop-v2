package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BundleProduct extends Product {

    @NotNull
    @Valid
    private final MailProduct mailProduct;

    @NotNull
    @Valid
    private final PartnerProduct partnerProduct;

    public BundleProduct(
            @NotNull @Valid final MailProduct mailProduct,
            @NotNull @Valid final PartnerProduct partnerProduct) {
        super(
                generateName(mailProduct, partnerProduct),
                validateMatchingBrands(mailProduct, partnerProduct),
                calculateTotalSetupFee(mailProduct, partnerProduct),
                calculateTotalMonthlyFee(mailProduct, partnerProduct));
        this.mailProduct = mailProduct;
        this.partnerProduct = partnerProduct;
    }

    @NotNull
    public MailProduct getMailProduct() {
        return mailProduct;
    }

    @NotNull
    public PartnerProduct getPartnerProduct() {
        return partnerProduct;
    }

    private static String generateName(
            final MailProduct mail,
            final PartnerProduct partner) {
        validateComponents(mail, partner);
        return "Bundle: " + mail.getName() + " " + partner.getName();
    }

    private static Brand validateMatchingBrands(
            final MailProduct mail,
            final PartnerProduct partner) {
        validateComponents(mail, partner);
        if (mail.getBrand() != partner.getBrand()) {
            throw new IllegalArgumentException("Brands must match for bundle product");
        }
        return mail.getBrand();
    }

    private static BigDecimal calculateTotalSetupFee(
            final MailProduct mail,
            final PartnerProduct partner) {
        validateComponents(mail, partner);
        return mail.getSetupFee().add(partner.getSetupFee());
    }

    private static BigDecimal calculateTotalMonthlyFee(
            final MailProduct mail,
            final PartnerProduct partner) {
        validateComponents(mail, partner);
        return mail.getMonthlyFee().add(partner.getMonthlyFee());
    }

    private static void validateComponents(
            final MailProduct mail,
            final PartnerProduct partner) {
        if (mail == null || partner == null) {
            throw new IllegalArgumentException("Mail and Partner products must not be null");
        }
    }
}
