package dev.mam.buizsol.mamshop.product.validation;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidStorageSizeValidator implements ConstraintValidator<ValidStorageSize, Long> {

    @Override
    public boolean isValid(@Nullable final Long value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value >= 1L;
    }
}
