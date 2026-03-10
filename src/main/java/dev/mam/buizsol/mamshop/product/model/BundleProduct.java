package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record BundleProduct(
        @NotNull UUID id,

        @NotBlank @Size(max = 100, message = "Product name must not exceed 100 characters")
        String name,

        @NotNull Brand brand,
        @NotNull @DecimalMin(value = "0.00") BigDecimal setupFee,
        @NotNull @DecimalMin(value = "0.11") BigDecimal monthlyFee,
        @NotNull @Valid Product mailProduct,
        @NotNull @Valid Product partnerProduct)
        implements Product {

    public BundleProduct(@NotNull @Valid final Product mailProduct, @NotNull @Valid final Product partnerProduct) {
        this(
                UUID.randomUUID(),
                generateBundleName(mailProduct, partnerProduct),
                validateBrands(mailProduct, partnerProduct),
                calculateTotalSetupFee(mailProduct, partnerProduct),
                calculateTotalMonthlyFee(mailProduct, partnerProduct),
                mailProduct,
                partnerProduct);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Brand getBrand() {
        return brand;
    }

    @Override
    public BigDecimal getSetupFee() {
        return setupFee;
    }

    @Override
    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    @Override
    @NotNull
    public BundleProduct withMonthlyFee(@NotNull final BigDecimal monthlyFee) {
        throw new UnsupportedOperationException("Monthly fee is calculated from components and cannot be set manually");
    }

    private static String generateBundleName(final Product mail, final Product partner) {
        if (mail == null || partner == null) {
            throw new ProductValidationException("Mail and Partner products must not be null");
        }
        return "Bundle: " + mail.getName() + " " + partner.getName();
    }

    private static BigDecimal calculateTotalSetupFee(final Product mail, final Product partner) {
        if (mail == null || partner == null) {
            return BigDecimal.ZERO;
        }
        return mail.getSetupFee().add(partner.getSetupFee());
    }

    private static BigDecimal calculateTotalMonthlyFee(final Product mail, final Product partner) {
        if (mail == null || partner == null) {
            return BigDecimal.ZERO;
        }
        return mail.getMonthlyFee().add(partner.getMonthlyFee());
    }

    private static Brand validateBrands(final Product mail, final Product partner) {
        if (mail == null || partner == null) {
            throw new ProductValidationException("Mail and Partner products must not be null");
        }
        if (mail.getBrand() != partner.getBrand()) {
            throw new ProductValidationException("Brands must match for bundle product");
        }
        return mail.getBrand();
    }
}
