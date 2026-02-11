package dev.mam.buizsol.mamshop.product.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateProductComponents;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateMatchingBrandsProduct;

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
                validateMatchingBrandsProduct(mailProduct, partnerProduct),
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

    @Override
    public void setMonthlyFee(
            @NotNull final BigDecimal monthlyFee) {
        throw new UnsupportedOperationException("Monthly fee is calculated from components and cannot be set manually");
    }

    @Override
    @NotNull
    public BigDecimal getMonthlyFee() {
        return calculateTotalMonthlyFee(mailProduct, partnerProduct);
    }

    private static String generateName(
            final MailProduct mail,
            final PartnerProduct partner) {
        validateProductComponents(mail, partner);
        return "Bundle: " + mail.getName() + " " + partner.getName();
    }

    private static BigDecimal calculateTotalSetupFee(
            final MailProduct mail,
            final PartnerProduct partner) {
        validateProductComponents(mail, partner);
        return mail.getSetupFee().add(partner.getSetupFee());
    }

    private static BigDecimal calculateTotalMonthlyFee(
            final MailProduct mail,
            final PartnerProduct partner) {
        validateProductComponents(mail, partner);
        return mail.getMonthlyFee().add(partner.getMonthlyFee());
    }
}
