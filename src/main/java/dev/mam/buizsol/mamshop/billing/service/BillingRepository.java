package dev.mam.buizsol.mamshop.billing.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

import dev.mam.buizsol.mamshop.billing.validation.InvoiceDiscount;
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
