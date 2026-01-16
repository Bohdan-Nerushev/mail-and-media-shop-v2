package com.unitedinternet.buizsol.mamshop.billing.service;

import com.unitedinternet.buizsol.mamshop.billing.model.Invoice;
import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import com.unitedinternet.buizsol.mamshop.product.exception.ProductNotFoundException;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;


public interface BillingService {

    @NotNull
    static BillingService getInstance() {
        return BillingServiceImpl.getInstance();
    }

    @NotNull
    Invoice generateInvoice(@NotNull UUID customerId) throws CustomerNotFoundException, ProductNotFoundException;

    @NotNull
    Invoice generateInvoice(@NotNull UUID customerId, @NotNull BigDecimal discount)
            throws CustomerNotFoundException, ProductNotFoundException;
}
