package dev.mam.buizsol.mamshop.config;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidationException;
import dev.mam.buizsol.mamshop.billing.validation.InvoiceDiscount;
import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.contract.validation.BrandMatch;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ValidationExceptionHandler Aspect Tests")
class ValidationExceptionHandlerTest {

    private ValidationExceptionHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new ValidationExceptionHandler();
    }

    private @interface ActiveCustomer {
    }

    @Test
    @DisplayName("Should map ActiveCustomer violation to CustomerNotActiveException")
    void shouldHandleActiveCustomerViolation() {
        ConstraintViolationException ex = createException(ActiveCustomer.class, "Message",
                "dev.mam.buizsol.mamshop.customer.model.ClassName");
        assertThrows(CustomerNotActiveException.class, () -> handler.handleValidationException(ex));
    }

    @Test
    @DisplayName("Should map InvoiceDiscount violation to InvalidInvoiceDiscountException")
    void shouldHandleInvoiceDiscountViolation() {
        ConstraintViolationException ex = createException(InvoiceDiscount.class, "Message",
                "dev.mam.buizsol.mamshop.billing.model.ClassName");
        assertThrows(InvalidInvoiceDiscountException.class, () -> handler.handleValidationException(ex));
    }

    @Test
    @DisplayName("Should map BrandMatch violation to BrandMismatchException")
    void shouldHandleBrandMatchViolation() {
        ConstraintViolationException ex = createException(BrandMatch.class, "Message",
                "dev.mam.buizsol.mamshop.contract.service.ClassName");
        assertThrows(BrandMismatchException.class, () -> handler.handleValidationException(ex));
    }

    @Test
    @DisplayName("Should map general billing package violation to InvoiceValidationException")
    void shouldHandleGeneralBillingViolation() {
        ConstraintViolationException ex = createException(NotNull.class, "Message",
                "dev.mam.buizsol.mamshop.billing.service.Impl");
        assertThrows(InvoiceValidationException.class, () -> handler.handleValidationException(ex));
    }

    @Test
    @DisplayName("Should map general product package violation to ProductValidationException")
    void shouldHandleGeneralProductViolation() {
        ConstraintViolationException ex = createException(NotNull.class, "Message",
                "dev.mam.buizsol.mamshop.product.domain.Data");
        assertThrows(ProductValidationException.class, () -> handler.handleValidationException(ex));
    }

    @Test
    @DisplayName("Should map general contract package violation to ContractValidationException")
    void shouldHandleGeneralContractViolation() {
        ConstraintViolationException ex = createException(NotNull.class, "Message",
                "dev.mam.buizsol.mamshop.contract.Entity");
        assertThrows(ContractValidationException.class, () -> handler.handleValidationException(ex));
    }

    @Test
    @DisplayName("Should map general customer package violation to CustomerValidationException")
    void shouldHandleGeneralCustomerViolation() {
        ConstraintViolationException ex = createException(NotNull.class, "Message",
                "dev.mam.buizsol.mamshop.customer.Service");
        assertThrows(CustomerValidationException.class, () -> handler.handleValidationException(ex));
    }

    private ConstraintViolationException createException(Class<? extends Annotation> annotationType, String message,
            String rootClassName) {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
        Annotation annotation = mock(Annotation.class);

        when(violation.getMessage()).thenReturn(message);
        when(violation.getConstraintDescriptor()).thenReturn(descriptor);
        when(descriptor.getAnnotation()).thenReturn(annotation);
        when(annotation.annotationType()).thenReturn((Class) annotationType);

        Class<?> billingClass = InvoiceDiscount.class;
        Class<?> productClass = dev.mam.buizsol.mamshop.product.model.MailProduct.class;
        Class<?> contractClass = dev.mam.buizsol.mamshop.contract.validation.BrandMatch.class;
        Class<?> customerClass = dev.mam.buizsol.mamshop.customer.model.Brand.class;
        Class<?> neutralClass = String.class;

        if (rootClassName.contains(".billing.")) {
            when(violation.getRootBeanClass()).thenReturn((Class) billingClass);
        } else if (rootClassName.contains(".product.")) {
            when(violation.getRootBeanClass()).thenReturn((Class) productClass);
        } else if (rootClassName.contains(".contract.")) {
            when(violation.getRootBeanClass()).thenReturn((Class) contractClass);
        } else if (rootClassName.contains(".customer.")) {
            when(violation.getRootBeanClass()).thenReturn((Class) customerClass);
        } else {
            when(violation.getRootBeanClass()).thenReturn((Class) neutralClass);
        }

        return new ConstraintViolationException(Set.of(violation));
    }

    @Test
    @DisplayName("Should rethrow original exception if no package/annotation mapping matches (fallback branch)")
    void shouldRethrowOriginalExceptionWhenNoMappingMatches() {
        ConstraintViolationException ex = createException(NotNull.class, "Fallback",
                "dev.mam.buizsol.mamshop.unknown.UnknownService");

        assertThrows(ConstraintViolationException.class, () -> handler.handleValidationException(ex));
    }
}
