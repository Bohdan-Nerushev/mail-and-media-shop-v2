package dev.mam.buizsol.mamshop.shop.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.billing.service.BillingService;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.contract.service.ContractService;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.service.CustomerService;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import jakarta.annotation.PostConstruct;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateCustomerActive;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateBrandMatch;
import dev.mam.buizsol.mamshop.product.service.ProductCatalogLoader;

import java.util.List;
import java.util.UUID;

@Validated
final class ShopServiceImpl implements ShopService {

    private final CustomerService customerService;
    private final ProductService productService;
    private final ContractService contractService;
    private final BillingService billingService;
    private final ProductCatalogLoader productCatalogLoader;

    private static final String CSV_PATH = "/products.csv";

    ShopServiceImpl(
            final CustomerService customerService,
            final ProductService productService,
            final ContractService contractService,
            final BillingService billingService,
            final ProductCatalogLoader productCatalogLoader) {
        this.customerService = customerService;
        this.productService = productService;
        this.contractService = contractService;
        this.billingService = billingService;
        this.productCatalogLoader = productCatalogLoader;
    }

    @PostConstruct
    private void init() {
        productCatalogLoader.load(CSV_PATH);
    }

    @Override
    @NotNull
    public Customer registerCustomer(
            @NotNull @Valid final Customer customer) {
        return customerService.createCustomer(customer);
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
            @NotNull final Brand brand) {
        return List.copyOf(productService.findByBrand(brand));
    }

    @Override
    @NotNull
    public Contract purchaseProduct(
            @NotNull final UUID customerId,
            @NotNull final UUID productId)
            throws CustomerNotFoundException, ProductNotFoundException {

        final Customer customer = loadCustomer(customerId);
        validateCustomerActive(customer);

        final Product product = productService.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        validateBrandMatch(customer, product);

        return contractService.createContract(customer, product);
    }

    private void checkCustomerActive(@NotNull final UUID customerId) throws CustomerNotFoundException {
        final Customer customer = loadCustomer(customerId);
        validateCustomerActive(customer);
    }
}
