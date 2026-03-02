package dev.mam.buizsol.mamshop.billing.mapper;

import dev.mam.buizsol.mamshop.billing.dto.InvoiceResponseDTO;
import dev.mam.buizsol.mamshop.billing.model.Invoice;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceResponseDTO toInvoiceResponseDTO (@NotNull Invoice invoice) {
        return new InvoiceResponseDTO(
                invoice.brand(),
                invoice.invoiceDate(),
                invoice.customerId(),
                invoice.address(),
                invoice.invoiceAddress(),
                invoice.items(),
                invoice.totalSetupFee(),
                invoice.totalMonthlyFee(),
                invoice.discount(),
                invoice.discount()
        );
    }
}
