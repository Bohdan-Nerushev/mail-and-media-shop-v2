package dev.mam.buizsol.mamshop.billing.model;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidateException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class Invoice {

    @NotNull
    private final Brand brand;

    @NotNull
    private final LocalDate invoiceDate;

    @NotNull
    private final UUID customerId;

    @NotNull
    private final Address address;

    @NotNull
    private final Address invoiceAddress;

    @NotNull
    private final List<InvoiceItem> items;

    @NotNull
    private final BigDecimal totalSetupFee;

    @NotNull
    private final BigDecimal totalMonthlyFee;

    @NotNull
    @PositiveOrZero
    private final BigDecimal discount;

    @NotNull
    private final BigDecimal totalAmount;

    public Invoice(
            @NotNull Brand brand,
            @NotNull UUID customerId,
            @NotNull Address address,
            @NotNull Address invoiceAddress,
            @NotNull List<InvoiceItem> items,
            @NotNull BigDecimal discount) {
        validateNotNull(items, "Items list");
        validateDiscount(discount);

        this.brand = brand;
        this.customerId = customerId;
        this.address = address;
        this.invoiceAddress = invoiceAddress;
        this.items = List.copyOf(items);
        this.discount = discount;
        this.invoiceDate = LocalDate.now();

        this.totalSetupFee = calculateTotalSetupFee();
        this.totalMonthlyFee = calculateTotalMonthlyFee();
        this.totalAmount = totalSetupFee.add(totalMonthlyFee).subtract(discount);
    }

    private BigDecimal calculateTotalSetupFee() {
        return items.stream()
                .map(InvoiceItem::getSetupFee)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    private BigDecimal calculateTotalMonthlyFee() {
        return items.stream()
                .map(InvoiceItem::getMonthlyFee)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    @NotNull
    public Brand getBrand() {
        return brand;
    }

    @NotNull
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    @NotNull
    public UUID getCustomerId() {
        return customerId;
    }

    @NotNull
    public Address getAddress() {
        return address;
    }

    @NotNull
    public Address getInvoiceAddress() {
        return invoiceAddress;
    }

    @NotNull
    public List<InvoiceItem> getItems() {
        return items;
    }

    @NotNull
    public BigDecimal getTotalSetupFee() {
        return totalSetupFee;
    }

    @NotNull
    public BigDecimal getTotalMonthlyFee() {
        return totalMonthlyFee;
    }

    @NotNull
    public BigDecimal getDiscount() {
        return discount;
    }

    @NotNull
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    protected void validateNotNull(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new InvoiceValidateException(fieldName + " must not be null");
        }
    }

    protected void validateDiscount(
            @NotNull final BigDecimal discount) {
        if (discount == null) {
            throw new InvalidInvoiceDiscountException("Discount must not be null");
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInvoiceDiscountException("Discount cannot be negative");
        }
    }
}
