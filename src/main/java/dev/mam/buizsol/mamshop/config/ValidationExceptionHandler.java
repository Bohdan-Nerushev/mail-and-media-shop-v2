package dev.mam.buizsol.mamshop.config;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidationException;
import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
public class ValidationExceptionHandler {

    @AfterThrowing(pointcut = "execution(* dev.mam.buizsol.mamshop..service..*(..))", throwing = "ex")
    public void handleValidationException(final ConstraintViolationException ex) {
        final Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (final ConstraintViolation<?> violation : violations) {
            final String message = violation.getMessage();
            final Class<?> annotationType = violation.getConstraintDescriptor().getAnnotation().annotationType();
            final String annotationName = annotationType.getSimpleName();


            if ("ActiveCustomer".equals(annotationName)) {
                throw new CustomerNotActiveException(message);
            }
            if ("InvoiceDiscount".equals(annotationName)) {
                throw new InvalidInvoiceDiscountException(message);
            }
            if ("BrandMatch".equals(annotationName)) {
                throw new BrandMismatchException(message);
            }


            final String className = violation.getRootBeanClass().getName();
            final String annotationPackage = annotationType.getPackage().getName();

            if (className.contains(".billing.") || annotationPackage.contains(".billing.")) {
                throw new InvoiceValidationException(message);
            }
            if (className.contains(".product.") || annotationPackage.contains(".product.")) {
                throw new ProductValidationException(message);
            }
            if (className.contains(".customer.") || className.contains(".shop.")
                    || annotationPackage.contains(".customer.")) {
                throw new CustomerValidationException(message);
            }
            if (className.contains(".contract.") || annotationPackage.contains(".contract.")) {
                throw new ContractValidationException(message);
            }
        }

        throw ex;
    }
}
