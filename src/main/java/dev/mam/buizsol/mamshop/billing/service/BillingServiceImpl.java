package dev.mam.buizsol.mamshop.billing.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.billing.model.InvoiceItem;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateDiscount;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullInvoice;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.contract.service.ContractService;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.service.CustomerService;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.service.ProductService;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Validated
final class BillingServiceImpl implements BillingService {

    private final CustomerService customerService;
    private final ProductService productService;
    private final ContractService contractService;

    BillingServiceImpl(
            @NotNull final CustomerService customerService,
            @NotNull final ProductService productService,
            @NotNull final ContractService contractService) {
        this.customerService = customerService;
        this.productService = productService;
        this.contractService = contractService;
    }

    @Override
    @NotNull
    public Invoice generateInvoice(@NotNull final UUID customerId)
            throws CustomerNotFoundException, ProductNotFoundException {
        return generateInvoice(customerId, BigDecimal.ZERO);
    }

    @Override
    @NotNull
    public Invoice generateInvoice(@NotNull final UUID customerId, @NotNull final BigDecimal discount)
            throws CustomerNotFoundException, ProductNotFoundException {
        validateNotNullInvoice(customerId, "Customer ID");
        validateDiscount(discount);

        final Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found"));

        final List<Contract> contracts = contractService.findContractsByCustomerId(customerId);
        final List<InvoiceItem> items = new ArrayList<>();

        for (final Contract contract : contracts) {
            if (contract.getStatus() == ContractStatus.ACTIVE) {
                final Product product = productService.findById(contract.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException("Product with ID " + contract.getProductId()
                                + " not found for contract " + contract.getId()));

                items.add(new InvoiceItem(
                        product.getId(),
                        product.getName(),
                        contract.getId(),
                        contract.getCreationDate(),
                        product.getSetupFee(),
                        product.getMonthlyFee()));
            }
        }

        return new Invoice(
                customer.getBrand(),
                customer.getId(),
                customer.getAddress(),
                customer.getInvoiceAddress(),
                items,
                discount);
    }
}
