package dev.mam.buizsol.mamshop.billing.validation;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class InvoiceDiscountValidator implements ConstraintValidator<InvoiceDiscount, BigDecimal> {

    @Override
    public boolean isValid(
            @Nullable final BigDecimal value,
            final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Discount cannot be negative")
                    .addConstraintViolation();
            return false;
        }
        if (value.compareTo(BigDecimal.ZERO) > 0 && value.compareTo(new BigDecimal("0.10")) <= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Discount must be greater than 0.10 €")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
