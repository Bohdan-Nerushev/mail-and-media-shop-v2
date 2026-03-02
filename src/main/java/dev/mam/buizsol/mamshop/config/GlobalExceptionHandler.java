package dev.mam.buizsol.mamshop.config;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidationException;
import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.MDC;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private String getCorrelationId() {
                return Optional.ofNullable(MDC.get("correlationId"))
                                .orElse("no-id");
        }

        @ExceptionHandler(CustomerNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex) {
                return ResponseEntity.status(404).body(new ErrorResponse(
                                getCorrelationId(),
                                "CUSTOMER_NOT_FOUND",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(CustomerNotActiveException.class)
        public ResponseEntity<ErrorResponse> handleCustomerNotActiveException(CustomerNotActiveException ex) {
                return ResponseEntity.status(409).body(new ErrorResponse(
                                getCorrelationId(),
                                "CUSTOMER_NOT_ACTIVE",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(CustomerValidationException.class)
        public ResponseEntity<ErrorResponse> handleCustomerValidationException(CustomerValidationException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "CUSTOMER_VALIDATION_ERROR",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
                return ResponseEntity.status(404).body(new ErrorResponse(
                                getCorrelationId(),
                                "PRODUCT_NOT_FOUND",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(ProductValidationException.class)
        public ResponseEntity<ErrorResponse> handleProductValidationException(ProductValidationException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "PRODUCT_VALIDATION_ERROR",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(ContractNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleContractNotFoundException(ContractNotFoundException ex) {
                return ResponseEntity.status(404).body(new ErrorResponse(
                                getCorrelationId(),
                                "CONTRACT_NOT_FOUND",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(ContractValidationException.class)
        public ResponseEntity<ErrorResponse> handleContractValidationException(ContractValidationException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "CONTRACT_VALIDATION_ERROR",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(BrandMismatchException.class)
        public ResponseEntity<ErrorResponse> handleBrandMismatchException(BrandMismatchException ex) {
                return ResponseEntity.status(422).body(new ErrorResponse(
                                getCorrelationId(),
                                "BRAND_MISMATCH",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(InvalidInvoiceDiscountException.class)
        public ResponseEntity<ErrorResponse> handleInvalidInvoiceDiscountException(InvalidInvoiceDiscountException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "INVALID_DISCOUNT",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(InvoiceValidationException.class)
        public ResponseEntity<ErrorResponse> handleInvoiceValidationException(InvoiceValidationException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "INVOICE_VALIDATION_ERROR",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "REQUEST_VALIDATION_ERROR",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(HandlerMethodValidationException.class)
        public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
                        HandlerMethodValidationException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "REQUEST_VALIDATION_ERROR",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "CONSTRAINT_VIOLATION",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
                        MissingServletRequestParameterException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "MISSING_PARAMETER",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex) {
                return ResponseEntity.status(400).body(new ErrorResponse(
                                getCorrelationId(),
                                "TYPE_MISMATCH",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
                return ResponseEntity.status(404).body(
                                new ErrorResponse(
                                                getCorrelationId(),
                                                "RESOURCE_NOT_FOUND",
                                                ex.getMessage(),
                                                LocalDateTime.now()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(Exception ex) {
                return ResponseEntity.status(500).body(new ErrorResponse(
                                getCorrelationId(),
                                "INTERNAL_ERROR",
                                ex.getMessage(),
                                LocalDateTime.now()));
        }
}
