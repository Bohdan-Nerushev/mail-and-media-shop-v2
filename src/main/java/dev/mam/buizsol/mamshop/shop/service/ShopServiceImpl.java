package dev.mam.buizsol.mamshop.shop.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.billing.service.BillingService;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.contract.service.ContractService;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import dev.mam.buizsol.mamshop.customer.service.CustomerService;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.service.ProductService;
import dev.mam.buizsol.mamshop.shop.exception.CustomerAndProductBrandMismatchException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopServiceImpl implements ShopService {

    private static final ShopServiceImpl INSTANCE = new ShopServiceImpl();

    private final CustomerService customerService;
    private final ProductService productService;
    private final ContractService contractService;
    private final BillingService billingService;

    private ShopServiceImpl() {
        this.customerService = CustomerService.getInstance();
        this.productService = ProductService.getInstance();
        this.contractService = ContractService.getInstance();
        this.billingService = BillingService.getInstance();
    }

    public static ShopServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public Customer registerCustomer(
            @NotNull @Valid final Customer customer) {
        return customerService.createCustomer(customer);
    }

    @Override
    @NotNull
    public Product registerProduct(
            @NotNull @Valid final Product product) {
        productService.createProduct(product);
        return product;
    }

    @Override
    @NotNull
    public Customer loadCustomer(
            @NotNull final UUID customerId) throws CustomerNotFoundException {
        return customerService.findCustomerById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
    }

    @Override
    public void removeCustomer(
            @NotNull final UUID customerId) throws CustomerNotFoundException {
        customerService.deleteCustomer(customerId);
    }

    @Override
    public void activateCustomer(
            @NotNull final UUID customerId) throws CustomerNotFoundException {
        customerService.activateCustomer(customerId);
    }

    @Override
    @NotNull
    public Customer updateAddress(
            @NotNull final UUID customerId,
            @NotNull @Valid final Address address) throws CustomerNotFoundException, CustomerNotActiveException {
        checkCustomerActive(customerId);
        customerService.updateAddress(customerId, address);
        return loadCustomer(customerId);
    }

    @Override
    @NotNull
    public Customer updateInvoiceAddress(
            @NotNull final UUID customerId,
            @NotNull @Valid final Address invoiceAddress) throws CustomerNotFoundException, CustomerNotActiveException {
        checkCustomerActive(customerId);
        customerService.updateInvoiceAddress(customerId, invoiceAddress);
        return loadCustomer(customerId);
    }

    @Override
    @NotNull
    public Customer updateCommunicationDetails(
            @NotNull final UUID customerId,
            @NotNull @Valid final CommunicationDetails details)
            throws CustomerNotFoundException, CustomerNotActiveException {
        checkCustomerActive(customerId);
        customerService.updateCommunicationDetails(customerId, details);
        return loadCustomer(customerId);
    }

    @Override
    @NotNull
    public List<Contract> loadAllContracts(
            @NotNull final UUID customerId) throws CustomerNotFoundException, CustomerNotActiveException {
        checkCustomerActive(customerId);
        return contractService.findContractsByCustomerId(customerId);
    }

    @Override
    @NotNull
    public Invoice generateInvoice(
            @NotNull final UUID customerId) throws CustomerNotFoundException, CustomerNotActiveException {
        checkCustomerActive(customerId);
        try {
            return billingService.generateInvoice(customerId);
        } catch (ProductNotFoundException e) {
            throw new RuntimeException("Unexpected error: Product not found during invoice generation", e);
        }
    }

    @Override
    public void activateContract(
            @NotNull final UUID contractId) throws ContractNotFoundException {
        contractService.updateContractStatus(contractId, ContractStatus.ACTIVE);
    }

    @Override
    @NotNull
    public List<Product> loadAllProductsForBrand(
            final @NotNull @Valid Brand brand) {
        return new ArrayList<>(productService.findByBrand(brand));
    }

    @Override
    @NotNull
    public Contract purchaseProduct(
            @NotNull final UUID customerId,
            @NotNull final UUID productId)
            throws CustomerNotFoundException, ProductNotFoundException, CustomerAndProductBrandMismatchException,
            CustomerNotActiveException {

        final Customer customer = loadCustomer(customerId);
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotActiveException("Customer must be active to purchase products");
        }

        final Product product = productService.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (customer.getBrand() != product.getBrand()) {
            throw new CustomerAndProductBrandMismatchException(
                    "Customer brand " + customer.getBrand() + " does not match product brand " + product.getBrand());
        }

        return contractService.createContract(customer, product);
    }

    private void checkCustomerActive(@NotNull final UUID customerId)
            throws CustomerNotFoundException, CustomerNotActiveException {
        final Customer customer = loadCustomer(customerId);
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotActiveException("Customer is not active");
        }
    }
}
