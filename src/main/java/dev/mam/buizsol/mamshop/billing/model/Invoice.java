package dev.mam.buizsol.mamshop.billing.model;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidationException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Invoice(
        @NotNull Brand brand,
        @NotNull LocalDate invoiceDate,
        @NotNull UUID customerId,
        @NotNull @Valid Address address,
        @NotNull @Valid Address invoiceAddress,
        @NotNull List<@Valid InvoiceItem> items,
        @NotNull BigDecimal totalSetupFee,
        @NotNull BigDecimal totalMonthlyFee,
        @NotNull @PositiveOrZero BigDecimal discount,
        @NotNull BigDecimal totalAmount) {

    public Invoice {
        if (items == null)
            throw new InvoiceValidationException("Items list must not be null");
        if (discount == null)
            throw new InvalidInvoiceDiscountException("Discount must not be null");
        if (discount.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidInvoiceDiscountException("Discount must not be negative");
        items = List.copyOf(items);
    }

    public Invoice(
            @NotNull Brand brand,
            @NotNull UUID customerId,
            @NotNull @Valid Address address,
            @NotNull @Valid Address invoiceAddress,
            @NotNull List<@Valid InvoiceItem> items,
            @NotNull BigDecimal discount) {
        this(
                brand,
                LocalDate.now(),
                customerId,
                address,
                invoiceAddress,
                items,
                calculateTotalSetupFee(items),
                calculateTotalMonthlyFee(items),
                discount,
                calculateTotalAmount(items, discount));
    }

    private static BigDecimal calculateTotalSetupFee(List<InvoiceItem> items) {
        if (items == null)
            return BigDecimal.ZERO;
        return items.stream()
                .map(InvoiceItem::setupFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTotalMonthlyFee(List<InvoiceItem> items) {
        if (items == null)
            return BigDecimal.ZERO;
        return items.stream()
                .map(InvoiceItem::monthlyFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTotalAmount(List<InvoiceItem> items, BigDecimal discount) {
        if (discount == null)
            throw new InvalidInvoiceDiscountException("Discount must not be null");
        if (items == null)
            throw new InvoiceValidationException("Items list must not be null");
        return calculateTotalSetupFee(items)
                .add(calculateTotalMonthlyFee(items))
                .subtract(discount);
    }
}
