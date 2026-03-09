package dev.mam.buizsol.mamshop.billing.mapper;

import dev.mam.buizsol.mamshop.billing.dto.InvoiceResponseDTO;
import dev.mam.buizsol.mamshop.billing.model.Invoice;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceResponseDTO toInvoiceResponseDTO(@NotNull Invoice invoice) {
        return new InvoiceResponseDTO(
                invoice.getBrand(),
                invoice.getInvoiceDate(),
                invoice.getCustomer().getId(),
                invoice.getAddress(),
                invoice.getInvoiceAddress(),
                invoice.getItems(),
                invoice.getTotalSetupFee(),
                invoice.getTotalMonthlyFee(),
                invoice.getDiscount(),
                invoice.getDiscount());
    }
}
