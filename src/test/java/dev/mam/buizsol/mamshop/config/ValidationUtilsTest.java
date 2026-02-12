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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ValidationUtils Test")
class ValidationUtilsTest {

    @Test
    @DisplayName("shouldThrowInvoiceValidationExceptionWhenValueIsNull")
    void shouldThrowInvoiceValidationExceptionWhenValueIsNull() {
        assertThrows(InvoiceValidationException.class,
                () -> ValidationUtils.validateNotNullInvoice(null, "Test Field"));
    }

    @Test
    @DisplayName("shouldNotThrowExceptionWhenInvoiceValueIsNotNull")
    void shouldNotThrowExceptionWhenInvoiceValueIsNotNull() {
        assertDoesNotThrow(() -> ValidationUtils.validateNotNullInvoice(new Object(), "Test Field"));
    }

    @Test
    @DisplayName("shouldThrowInvalidInvoiceDiscountExceptionWhenDiscountIsNull")
    void shouldThrowInvalidInvoiceDiscountExceptionWhenDiscountIsNull() {
        assertThrows(InvalidInvoiceDiscountException.class, () -> ValidationUtils.validateDiscount(null));
    }

    @Test
    @DisplayName("shouldThrowInvalidInvoiceDiscountExceptionWhenDiscountIsNegative")
    void shouldThrowInvalidInvoiceDiscountExceptionWhenDiscountIsNegative() {
        assertThrows(InvalidInvoiceDiscountException.class,
                () -> ValidationUtils.validateDiscount(new BigDecimal("-1.00")));
    }

    @Test
    @DisplayName("shouldNotThrowExceptionWhenDiscountIsZero")
    void shouldNotThrowExceptionWhenDiscountIsZero() {
        assertDoesNotThrow(() -> ValidationUtils.validateDiscount(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("shouldNotThrowExceptionWhenDiscountIsPositive")
    void shouldNotThrowExceptionWhenDiscountIsPositive() {
        assertDoesNotThrow(() -> ValidationUtils.validateDiscount(new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("shouldThrowBrandMismatchExceptionWhenBrandsDoNotMatch")
    void shouldThrowBrandMismatchExceptionWhenBrandsDoNotMatch() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);
        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getBrand()).thenReturn(Brand.WEB_DE);

        assertThrows(BrandMismatchException.class, () -> ValidationUtils.validateBrandMatch(customer, product));
    }

    @Test
    @DisplayName("shouldNotThrowExceptionWhenBrandsMatch")
    void shouldNotThrowExceptionWhenBrandsMatch() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);
        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getBrand()).thenReturn(Brand.GMX);

        assertDoesNotThrow(() -> ValidationUtils.validateBrandMatch(customer, product));
    }

    @Test
    @DisplayName("shouldThrowCustomerNotActiveExceptionWhenCustomerIsInactive")
    void shouldThrowCustomerNotActiveExceptionWhenCustomerIsInactive() {
        Customer customer = mock(Customer.class);
        when(customer.getStatus()).thenReturn(CustomerStatus.INACTIVE);
        when(customer.getId()).thenReturn(UUID.randomUUID());

        assertThrows(CustomerNotActiveException.class, () -> ValidationUtils.validateCustomerActive(customer));
    }

    @Test
    @DisplayName("shouldNotThrowExceptionWhenCustomerIsActive")
    void shouldNotThrowExceptionWhenCustomerIsActive() {
        Customer customer = mock(Customer.class);
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);

        assertDoesNotThrow(() -> ValidationUtils.validateCustomerActive(customer));
    }

    @Test
    @DisplayName("shouldThrowContractValidationExceptionWhenValueIsNull")
    void shouldThrowContractValidationExceptionWhenValueIsNull() {
        assertThrows(ContractValidationException.class,
                () -> ValidationUtils.validateNotNullContract(null, "Test Field"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("shouldThrowCustomerValidationExceptionWhenModelValueIsBlank")
    void shouldThrowCustomerValidationExceptionWhenModelValueIsBlank(String value) {
        assertThrows(CustomerValidationException.class,
                () -> ValidationUtils.validateCustomerModel(value, "Test Field"));
    }

    @Test
    @DisplayName("shouldThrowCustomerValidationExceptionWhenCustomerValueIsNull")
    void shouldThrowCustomerValidationExceptionWhenCustomerValueIsNull() {
        assertThrows(CustomerValidationException.class,
                () -> ValidationUtils.validateNotNullCustomer(null, "Test Field"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("shouldThrowCustomerValidationExceptionWhenCustomerValueIsBlank")
    void shouldThrowCustomerValidationExceptionWhenCustomerValueIsBlank(String value) {
        assertThrows(CustomerValidationException.class,
                () -> ValidationUtils.validateNotBlankCustomer(value, "Test Field"));
    }

    @Test
    @DisplayName("shouldThrowProductValidationExceptionWhenMonthlyFeeIsBelowOrAtThreshold")
    void shouldThrowProductValidationExceptionWhenMonthlyFeeIsBelowOrAtThreshold() {
        assertThrows(ProductValidationException.class,
                () -> ValidationUtils.validateMonthlyFeeProduct(new BigDecimal("0.10")));
        assertThrows(ProductValidationException.class,
                () -> ValidationUtils.validateMonthlyFeeProduct(new BigDecimal("0.05")));
    }

    @Test
    @DisplayName("shouldNotThrowExceptionWhenMonthlyFeeIsAboveThreshold")
    void shouldNotThrowExceptionWhenMonthlyFeeIsAboveThreshold() {
        assertDoesNotThrow(() -> ValidationUtils.validateMonthlyFeeProduct(new BigDecimal("0.11")));
    }

    @Test
    @DisplayName("shouldThrowProductValidationExceptionWhenSetupFeeIsNegative")
    void shouldThrowProductValidationExceptionWhenSetupFeeIsNegative() {
        assertThrows(ProductValidationException.class,
                () -> ValidationUtils.validateSetupFeeProduct(new BigDecimal("-0.01")));
    }

    @Test
    @DisplayName("shouldNotThrowExceptionWhenSetupFeeIsZeroOrPositive")
    void shouldNotThrowExceptionWhenSetupFeeIsZeroOrPositive() {
        assertDoesNotThrow(() -> ValidationUtils.validateSetupFeeProduct(BigDecimal.ZERO));
        assertDoesNotThrow(() -> ValidationUtils.validateSetupFeeProduct(new BigDecimal("1.00")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("shouldThrowProductValidationExceptionWhenNameIsBlank")
    void shouldThrowProductValidationExceptionWhenNameIsBlank(String value) {
        assertThrows(ProductValidationException.class,
                () -> ValidationUtils.validateNotBlankProduct(value, "Test Field"));
    }

    @Test
    @DisplayName("shouldThrowProductValidationExceptionWhenProductValueIsNull")
    void shouldThrowProductValidationExceptionWhenProductValueIsNull() {
        assertThrows(ProductValidationException.class,
                () -> ValidationUtils.validateNotNullProduct(null, "Test Field"));
    }

    @Test
    @DisplayName("shouldThrowProductValidationExceptionWhenAComponentIsNull")
    void shouldThrowProductValidationExceptionWhenAComponentIsNull() {
        MailProduct mail = mock(MailProduct.class);
        PartnerProduct partner = mock(PartnerProduct.class);

        assertThrows(ProductValidationException.class, () -> ValidationUtils.validateProductComponents(null, partner));
        assertThrows(ProductValidationException.class, () -> ValidationUtils.validateProductComponents(mail, null));
    }

    @Test
    @DisplayName("shouldThrowProductValidationExceptionWhenBundleBrandsMismatch")
    void shouldThrowProductValidationExceptionWhenBundleBrandsMismatch() {
        MailProduct mail = mock(MailProduct.class);
        PartnerProduct partner = mock(PartnerProduct.class);
        when(mail.getBrand()).thenReturn(Brand.GMX);
        when(partner.getBrand()).thenReturn(Brand.WEB_DE);

        assertThrows(ProductValidationException.class,
                () -> ValidationUtils.validateMatchingBrandsProduct(mail, partner));
    }

    @Test
    @DisplayName("shouldReturnBrandWhenBundleBrandsMatch")
    void shouldReturnBrandWhenBundleBrandsMatch() {
        MailProduct mail = mock(MailProduct.class);
        PartnerProduct partner = mock(PartnerProduct.class);
        when(mail.getBrand()).thenReturn(Brand.GMX);
        when(partner.getBrand()).thenReturn(Brand.GMX);

        Brand result = ValidationUtils.validateMatchingBrandsProduct(mail, partner);
        assertThat(result).isEqualTo(Brand.GMX);
    }
}
