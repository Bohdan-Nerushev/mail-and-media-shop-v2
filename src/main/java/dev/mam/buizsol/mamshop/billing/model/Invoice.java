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

public final class Invoice {

    @NotNull
    private final Brand brand;

    @NotNull
    private final LocalDate invoiceDate;

    @NotNull
    private final UUID customerId;

    @NotNull
    @Valid
    private final Address address;

    @NotNull
    @Valid
    private final Address invoiceAddress;

    @NotNull
    private final List<@Valid InvoiceItem> items;

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
            @NotNull @Valid Address address,
            @NotNull @Valid Address invoiceAddress,
            @NotNull List<@Valid InvoiceItem> items,
            @NotNull BigDecimal discount) {
        validateNotNullInvoice(items, "Items list");
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
                .map(InvoiceItem::setupFee)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    private BigDecimal calculateTotalMonthlyFee() {
        return items.stream()
                .map(InvoiceItem::monthlyFee)
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
}
