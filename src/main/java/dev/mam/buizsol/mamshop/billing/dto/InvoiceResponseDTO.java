package dev.mam.buizsol.mamshop.billing.dto;

import dev.mam.buizsol.mamshop.billing.model.InvoiceItem;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InvoiceResponseDTO(
        Brand brand,
        LocalDate invoiceDate,
        UUID customerId,
        Address address,
        Address invoiceAddress,
        List<InvoiceItem> items,
        BigDecimal totalSetupFee,
        BigDecimal totalMonthlyFee,
        BigDecimal discount,
        BigDecimal totalAmount) {
}
