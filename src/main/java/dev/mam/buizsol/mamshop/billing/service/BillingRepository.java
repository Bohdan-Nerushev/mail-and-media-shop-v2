package dev.mam.buizsol.mamshop.billing.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

@Validated
public interface BillingRepository extends JpaRepository<Invoice, UUID> {

    //    @NotNull
    //    Invoice generateInvoice(@NotNull UUID customerId) throws CustomerNotFoundException, ProductNotFoundException;
    //
    //    @NotNull
    //    Invoice generateInvoice(@NotNull UUID customerId, @NotNull @InvoiceDiscount BigDecimal discount)
    //            throws CustomerNotFoundException, ProductNotFoundException;

}
