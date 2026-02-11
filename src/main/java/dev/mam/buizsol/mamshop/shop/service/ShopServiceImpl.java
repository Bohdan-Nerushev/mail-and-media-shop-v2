package dev.mam.buizsol.mamshop.shop.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.billing.service.BillingService;
import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import jakarta.annotation.PostConstruct;

import dev.mam.buizsol.mamshop.product.service.ProductCatalogLoader;

import java.util.List;
import java.util.UUID;

@Service
@Validated
final class ShopServiceImpl implements ShopService {

    private final CustomerService customerService;
    private final ProductService productService;
    private final ContractService contractService;
    private final BillingService billingService;

    private static final String CSV_PATH = "/products.csv";

    @Autowired
    ShopServiceImpl(
            @NotNull final CustomerService customerService,
            @NotNull final ProductService productService,
            @NotNull final ContractService contractService,
            @NotNull final BillingService billingService) {
        this.customerService = customerService;
        this.productService = productService;
        this.contractService = contractService;
        this.billingService = billingService;
    }

    @PostConstruct
    private void init() {
        ProductCatalogLoader.load(this.productService, CSV_PATH);
    }

    @Override
    @NotNull
    public Customer registerCustomer(
            @NotNull @Valid final Customer customer) {
        return customerService.createCustomer(customer);
    }

    @NotNull
    protected Product registerProduct(
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
    public void deactivateCustomer(
            @NotNull final UUID customerId) throws CustomerNotFoundException {
        customerService.deactivateCustomer(customerId);
    }

    @Override
    @NotNull
    public Customer updateAddress(
            @NotNull final UUID customerId,
            @NotNull @Valid final Address address) throws CustomerNotFoundException {
        checkCustomerActive(customerId);
        customerService.updateAddress(customerId, address);
        return loadCustomer(customerId);
    }

    @Override
    @NotNull
    public Customer updateInvoiceAddress(
            @NotNull final UUID customerId,
            @NotNull @Valid final Address invoiceAddress) throws CustomerNotFoundException {
        checkCustomerActive(customerId);
        customerService.updateInvoiceAddress(customerId, invoiceAddress);
        return loadCustomer(customerId);
    }

    @Override
    @NotNull
    public Customer updateCommunicationDetails(
            @NotNull final UUID customerId,
            @NotNull @Valid final CommunicationDetails details) throws CustomerNotFoundException {
        checkCustomerActive(customerId);
        customerService.updateCommunicationDetails(customerId, details);
        return loadCustomer(customerId);
    }

    @Override
    @NotNull
    public List<Contract> loadAllContracts(
            @NotNull final UUID customerId) throws CustomerNotFoundException {
        checkCustomerActive(customerId);
        return List.copyOf(contractService.findContractsByCustomerId(customerId));
    }

    @Override
    @NotNull
    public Invoice generateInvoice(
            @NotNull final UUID customerId) throws CustomerNotFoundException, ProductNotFoundException {
        checkCustomerActive(customerId);
        return billingService.generateInvoice(customerId);
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
        return List.copyOf(productService.findByBrand(brand));
    }

    @Override
    @NotNull
    public Contract purchaseProduct(
            @NotNull final UUID customerId,
            @NotNull final UUID productId)
            throws CustomerNotFoundException, ProductNotFoundException, BrandMismatchException {

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

    private void checkCustomerActive(@NotNull final UUID customerId) throws CustomerNotFoundException {
        final Customer customer = loadCustomer(customerId);
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotActiveException("Customer is not active");
        }
    }
}
