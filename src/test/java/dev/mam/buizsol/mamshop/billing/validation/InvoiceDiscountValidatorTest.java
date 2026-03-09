//package dev.mam.buizsol.mamshop.billing.validation;
//
//import jakarta.validation.ConstraintValidatorContext;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@DisplayName("InvoiceDiscountValidator Tests")
//class InvoiceDiscountValidatorTest {
//
//    private InvoiceDiscountValidator validator;
//
//    @Mock
//    private ConstraintValidatorContext context;
//
//    @Mock
//    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        validator = new InvoiceDiscountValidator();
//
//        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
//        when(violationBuilder.addConstraintViolation()).thenReturn(context);
//    }
//
//    @Test
//    @DisplayName("Should return true when discount is null")
//    void shouldReturnTrueWhenValueIsNull() {
//        assertTrue(validator.isValid(null, context));
//    }
//
//    @Test
//    @DisplayName("Should return true when discount is zero")
//    void shouldReturnTrueWhenValueIsZero() {
//        assertTrue(validator.isValid(BigDecimal.ZERO, context));
//    }
//
//    @Test
//    @DisplayName("Should return true when discount is greater than 0.10")
//    void shouldReturnTrueWhenValueIsGreaterThanThreshold() {
//        assertTrue(validator.isValid(new BigDecimal("0.11"), context));
//        assertTrue(validator.isValid(new BigDecimal("5.00"), context));
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "0.01",
//            "0.05",
//            "0.10"
//    })
//    @DisplayName("Should return false when discount is between 0 (exclusive) and 0.10 (inclusive)")
//    void shouldReturnFalseWhenValueIsBelowThreshold(String value) {
//        assertFalse(validator.isValid(new BigDecimal(value), context));
//        verify(context).disableDefaultConstraintViolation();
//        verify(context).buildConstraintViolationWithTemplate("Discount must be greater than 0.10 €");
//    }
//
//    @Test
//    @DisplayName("Should return false when discount is negative")
//    void shouldReturnFalseWhenValueIsNegative() {
//        assertFalse(validator.isValid(new BigDecimal("-1.00"), context));
//        verify(context).disableDefaultConstraintViolation();
//        verify(context).buildConstraintViolationWithTemplate("Discount cannot be negative");
//    }
//}
