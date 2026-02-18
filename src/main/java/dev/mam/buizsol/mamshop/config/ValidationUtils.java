package dev.mam.buizsol.mamshop.config;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidationException;
import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import dev.mam.buizsol.mamshop.product.model.MailProduct;
import dev.mam.buizsol.mamshop.product.model.PartnerProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ValidationUtils {

    public static void validateNotNullInvoice(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new InvoiceValidationException(fieldName + " must not be null");
        }
    }

    public static void validateDiscount(
            @Nullable final BigDecimal discount) {
        if (discount == null) {
            throw new InvalidInvoiceDiscountException("Discount must not be null");
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInvoiceDiscountException("Discount cannot be negative");
        }
        if (discount.compareTo(BigDecimal.ZERO) > 0 && discount.compareTo(new BigDecimal("0.10")) <= 0) {
            throw new InvalidInvoiceDiscountException("Discount must be greater than 0.10 €");
        }
    }

    public static void validateBrandMatch(
            @NotNull final Customer customer,
            @NotNull final Product product) {
        if (!customer.getBrand().equals(product.getBrand())) {
            throw new BrandMismatchException(String.format(
                    "Customer brand %s does not match product brand %s",
                    customer.getBrand(),
                    product.getBrand()));
        }
    }

    public static void validateCustomerActive(
            @NotNull final Customer customer) {
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotActiveException(String.format(
                    "Customer %s is not active",
                    customer.getId()));
        }
    }

    public static void validateNotNullContract(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new ContractValidationException(fieldName + " must not be null");
        }
    }

    public static void validateCustomerModel(
            @Nullable final String value,
            @NotNull final String fieldName) {
        validateCustomerModel(value, fieldName, 100);
    }

    public static void validateCustomerModel(
            @Nullable final String value,
            @NotNull final String fieldName,
            final int maxLength) {
        if (value == null || value.isBlank()) {
            throw new CustomerValidationException(fieldName + " must not be null or empty");
        }
        if (value.length() > maxLength) {
            throw new CustomerValidationException(fieldName + " must not exceed " + maxLength + " characters");
        }
    }

    public static void validateNotNullCustomer(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new CustomerValidationException(fieldName + " must not be null");
        }
    }

    public static void validateNotBlankCustomer(
            @Nullable final String value,
            @NotNull final String fieldName) {
        validateNotBlankCustomer(value, fieldName, 100);
    }

    public static void validateNotBlankCustomer(
            @Nullable final String value,
            @NotNull final String fieldName,
            final int maxLength) {
        if (value == null || value.isBlank()) {
            throw new CustomerValidationException(fieldName + " must not be null or empty");
        }
        if (value.length() > maxLength) {
            throw new CustomerValidationException(fieldName + " must not exceed " + maxLength + " characters");
        }
    }

    public static void validateMonthlyFeeProduct(
            @Nullable final BigDecimal monthlyFee) {
        validateNotNullProduct(monthlyFee, "Monthly fee");
        if (monthlyFee.compareTo(new BigDecimal("0.10")) <= 0) {
            throw new ProductValidationException("Monthly fee must be greater than 0.10 €");
        }
    }

    public static void validateSetupFeeProduct(
            final BigDecimal setupFee) {
        if (setupFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductValidationException("Setup fee must not be negative");
        }
    }

    public static void validateNotBlankProduct(
            @Nullable final String value,
            @NotNull final String fieldName) {
        validateNotBlankProduct(value, fieldName, 100);
    }

    public static void validateNotBlankProduct(
            @Nullable final String value,
            @NotNull final String fieldName,
            final int maxLength) {
        if (value == null || value.isBlank()) {
            throw new ProductValidationException(fieldName + " must not be null or empty");
        }
        if (value.length() > maxLength) {
            throw new ProductValidationException(fieldName + " must not exceed " + maxLength + " characters");
        }
    }

    public static void validateNotNullProduct(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new ProductValidationException(fieldName + " must not be null");
        }
    }

    public static void validateProductComponents(
            final MailProduct mail,
            final PartnerProduct partner) {
        if (mail == null || partner == null) {
            throw new ProductValidationException("Mail and Partner products must not be null");
        }
    }

    public static Brand validateMatchingBrandsProduct(
            final MailProduct mail,
            final PartnerProduct partner) {
        validateProductComponents(mail, partner);
        if (mail.getBrand() != partner.getBrand()) {
            throw new ProductValidationException("Brands must match for bundle product");
        }
        return mail.getBrand();
    }

    public static void validateStorageSize(
            @Nullable final Long storageSize) {
        if (storageSize == null || storageSize < 1L) {
            throw new ProductValidationException("Storage size must be at least 1GB");
        }
    }
}
