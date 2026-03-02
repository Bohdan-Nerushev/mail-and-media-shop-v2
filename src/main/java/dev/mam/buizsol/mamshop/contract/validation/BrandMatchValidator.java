package dev.mam.buizsol.mamshop.contract.validation;

import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class BrandMatchValidator implements ConstraintValidator<BrandMatch, Object[]> {

    @Override
    public boolean isValid(
            final Object[] values,
            final ConstraintValidatorContext context) {
        if (values == null || values.length < 2) {
            return true;
        }

        if (!(values[0] instanceof Customer customer) || !(values[1] instanceof Product product)) {
            return true;
        }

        return customer.brand().equals(product.getBrand());
    }
}
