package dev.mam.buizsol.mamshop.billing.service;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidationException;
import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.billing.model.InvoiceItem;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.contract.service.ContractService;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.service.CustomerService;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
final class BillingServiceImpl implements BillingService {

    private final CustomerService customerService;
    private final ProductService productService;
    private final ContractService contractService;

    private final BigDecimal ZERO = BigDecimal.ZERO;
    private final BigDecimal DISCOUNT = new BigDecimal("0.10");

    BillingServiceImpl(
            final CustomerService customerService,
            final ProductService productService,
            final ContractService contractService) {
        this.customerService = customerService;
        this.productService = productService;
        this.contractService = contractService;
    }

    @Override
    public Invoice generateInvoice(
            final UUID customerId)
            throws CustomerNotFoundException, ProductNotFoundException {
        if (customerId == null) {
            throw new InvoiceValidationException("Customer ID must not be null");
        }
        return generateInvoice(customerId, ZERO);
    }

    @Override
    public Invoice generateInvoice(
            final UUID customerId,
            final BigDecimal discount)
            throws CustomerNotFoundException, ProductNotFoundException {

        if (customerId == null) {
            throw new InvoiceValidationException("Customer ID must not be null");
        }
        if (discount == null) {
            throw new InvalidInvoiceDiscountException("Discount must not be null");
        }
        if (discount.compareTo(ZERO) < 0) {
            throw new InvalidInvoiceDiscountException("Discount cannot be negative");
        }
        if (discount.compareTo(ZERO) > 0 && discount.compareTo(DISCOUNT) <= 0) {
            throw new InvalidInvoiceDiscountException("Discount must be greater than 0.10 €");
        }

        final Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found"));

        final List<Contract> contracts = contractService.findContractsByCustomerId(customerId);
        final List<InvoiceItem> items = new ArrayList<>();

        for (final Contract contract : contracts) {
            if (contract.status() == ContractStatus.ACTIVE) {
                final Product product = productService.findById(contract.productId())
                        .orElseThrow(() -> new ProductNotFoundException("Product with ID " + contract.productId()
                                + " not found for contract " + contract.id()));

                items.add(new InvoiceItem(
                        product.getId(),
                        product.getName(),
                        contract.id(),
                        contract.creationDate(),
                        product.getSetupFee(),
                        product.getMonthlyFee()));
            }
        }

        return new Invoice(
                customer.brand(),
                customer.id(),
                customer.address(),
                customer.invoiceAddress(),
                items,
                discount);
    }
}
