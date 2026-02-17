package dev.mam.buizsol.mamshop.billing.model;

import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateDiscount;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullInvoice;

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
        validateNotNullInvoice(items, "Items list");
        validateDiscount(discount);
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
        validateNotNullInvoice(items, "Items list");
        return items.stream()
                .map(InvoiceItem::setupFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTotalMonthlyFee(List<InvoiceItem> items) {
        validateNotNullInvoice(items, "Items list");
        return items.stream()
                .map(InvoiceItem::monthlyFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTotalAmount(List<InvoiceItem> items, BigDecimal discount) {
        validateDiscount(discount);
        return calculateTotalSetupFee(items)
                .add(calculateTotalMonthlyFee(items))
                .subtract(discount);
    }
}
