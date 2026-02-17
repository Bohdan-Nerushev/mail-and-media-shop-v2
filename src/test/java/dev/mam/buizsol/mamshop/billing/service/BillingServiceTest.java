package dev.mam.buizsol.mamshop.billing.service;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidationException;
import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.billing.model.InvoiceItem;
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
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
import dev.mam.buizsol.mamshop.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillingService Tests")
class BillingServiceTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private ProductService productService;

    @Mock
    private ContractService contractService;

    private BillingService billingService;

    private Customer testCustomer;
    private Product testProduct;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        billingService = new BillingServiceImpl(customerService, productService, contractService);

        Address address = new Address("Street", "1", "12345", "City", "Country");
        CommunicationDetails communication = new CommunicationDetails("test@test.com", "123456789");

        testCustomer = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), address, null, communication, Brand.GMX);
        customerId = testCustomer.getId();

        testProduct = new StandardMailProduct("Standard Mail", Brand.GMX, new BigDecimal("5.00"));
    }

    @Test
    @DisplayName("Successful generation of invoice without discount")
    void shouldGenerateInvoiceWithoutDiscountWhenAllRequirementsAreMet() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        Contract contract = mock(Contract.class);
        when(contract.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(contract.getProductId()).thenReturn(testProduct.getId());
        when(contract.getId()).thenReturn(UUID.randomUUID());
        when(contract.getCreationDate()).thenReturn(LocalDate.now());

        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of(contract));
        when(productService.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Invoice invoice = billingService.generateInvoice(customerId);

        assertNotNull(invoice);
        assertEquals(customerId, invoice.customerId());
        assertEquals(BigDecimal.ZERO, invoice.discount());
        assertEquals(1, invoice.items().size());
    }

    @Test
    @DisplayName("Successful generation of invoice with discount")
    void shouldGenerateInvoiceWithDiscountWhenValidDiscountProvided() throws Exception {
        BigDecimal discount = new BigDecimal("1.00");
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));
        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of());

        Invoice invoice = billingService.generateInvoice(customerId, discount);

        assertNotNull(invoice);
        assertEquals(discount, invoice.discount());
    }

    @Test
    @DisplayName("Verification of correct totals calculation in invoice")
    void shouldCalculateTotalsCorrectlyWhenMultipleContractsExist() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        Product p1 = new StandardMailProduct("P1", Brand.GMX, new BigDecimal("5.00"));
        Product p2 = new StandardMailProduct("P2", Brand.GMX, new BigDecimal("15.00"));

        Contract c1 = mock(Contract.class);
        when(c1.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(c1.getProductId()).thenReturn(p1.getId());
        when(c1.getId()).thenReturn(UUID.randomUUID());
        when(c1.getCreationDate()).thenReturn(LocalDate.now());

        Contract c2 = mock(Contract.class);
        when(c2.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(c2.getProductId()).thenReturn(p2.getId());
        when(c2.getId()).thenReturn(UUID.randomUUID());
        when(c2.getCreationDate()).thenReturn(LocalDate.now());

        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of(c1, c2));
        when(productService.findById(p1.getId())).thenReturn(Optional.of(p1));
        when(productService.findById(p2.getId())).thenReturn(Optional.of(p2));

        BigDecimal discount = new BigDecimal("5.00");

        Invoice invoice = billingService.generateInvoice(customerId, discount);

        assertEquals(new BigDecimal("9.98"), invoice.totalSetupFee());
        assertEquals(new BigDecimal("20.00"), invoice.totalMonthlyFee());
        assertEquals(new BigDecimal("24.98"), invoice.totalAmount());
    }

    @Test
    @DisplayName("Verification of invoice item field structure")
    void shouldPopulateInvoiceItemCorrectlyWhenGeneratingInvoice() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));
        LocalDate creationDate = LocalDate.now().minusDays(10);
        Contract contract = mock(Contract.class);
        when(contract.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(contract.getProductId()).thenReturn(testProduct.getId());
        when(contract.getId()).thenReturn(UUID.randomUUID());
        when(contract.getCreationDate()).thenReturn(creationDate);

        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of(contract));
        when(productService.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Invoice invoice = billingService.generateInvoice(customerId);
        InvoiceItem item = invoice.items().get(0);

        assertEquals(testProduct.getId(), item.productId());
        assertEquals(testProduct.getName(), item.productName());
        assertEquals(contract.getId(), item.contractId());
        assertEquals(creationDate, item.contractCreationDate());
        assertEquals(testProduct.getSetupFee(), item.setupFee());
        assertEquals(testProduct.getMonthlyFee(), item.monthlyFee());
    }

    @DisplayName("Invalid small positive discount validation checks")
    @ParameterizedTest(name = "Invalid small positive discount validation - value: {0}")
    @ValueSource(strings = { "0.01", "0.05", "0.10" })
    void shouldThrowExceptionWhenDiscountIsSmallPositive(BigDecimal invalidDiscount) {
        assertThrows(InvalidInvoiceDiscountException.class,
                () -> billingService.generateInvoice(customerId, invalidDiscount));
    }

    @DisplayName("Invalid negative discount validation checks")
    @ParameterizedTest(name = "Invalid negative discount validation - value: {0}")
    @ValueSource(strings = { "-0.01", "-1.00" })
    void shouldThrowExceptionWhenDiscountIsNegative(BigDecimal invalidDiscount) {
        assertThrows(InvalidInvoiceDiscountException.class,
                () -> billingService.generateInvoice(customerId, invalidDiscount));
    }

    @Test
    @DisplayName("Handling scenario when customer is not found")
    void shouldThrowExceptionWhenCustomerDoesNotExist() {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> billingService.generateInvoice(customerId));
    }

    @Test
    @DisplayName("Successful generation for customer without active contracts")
    void shouldGenerateEmptyInvoiceWhenCustomerHasNoActiveContracts() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        Contract contract = mock(Contract.class);
        when(contract.getStatus()).thenReturn(ContractStatus.INACTIVE);
        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of(contract));

        Invoice invoice = billingService.generateInvoice(customerId);

        assertTrue(invoice.items().isEmpty());
        assertEquals(BigDecimal.ZERO, invoice.totalSetupFee());
        assertEquals(BigDecimal.ZERO, invoice.totalMonthlyFee());
        assertEquals(BigDecimal.ZERO, invoice.totalAmount());
    }

    @DisplayName("Null arguments validation for generateInvoice")
    @ParameterizedTest(name = "Null arguments validation - customerId={0}, discount={1}")
    @CsvSource(value = {
            "null, 10.00",
            "550e8400-e29b-41d4-a716-446655440000, null",
            "null, null"
    }, nullValues = { "null" })
    void shouldThrowExceptionWhenArgumentsAreNull(UUID cid, BigDecimal disc) {
        if (cid == null) {
            assertThrows(InvoiceValidationException.class, () -> billingService.generateInvoice(cid, disc));
        } else {
            assertThrows(InvalidInvoiceDiscountException.class, () -> billingService.generateInvoice(cid, disc));
        }
    }

    @Test
    @DisplayName("Handling scenario when product is not found for a contract")
    void shouldThrowExceptionWhenProductInContractDoesNotExist() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));
        Contract contract = mock(Contract.class);
        when(contract.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(contract.getProductId()).thenReturn(UUID.randomUUID());
        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of(contract));
        when(productService.findById(any())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> billingService.generateInvoice(customerId));
    }

    @DisplayName("Verification of valid boundary discount values")
    @ParameterizedTest(name = "Boundary discount values - value: {0}")
    @ValueSource(strings = { "0.00", "0.11", "0.10001", "1.00", "5.00", "100.00" })
    void shouldGenerateInvoiceWhenValidBoundaryDiscountsProvided(BigDecimal validDiscount) throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));
        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of());

        Invoice invoice = billingService.generateInvoice(customerId, validDiscount);

        assertNotNull(invoice);
        assertEquals(validDiscount, invoice.discount());
    }

    @Test
    @DisplayName("Generation with mixed contract states (active and inactive)")
    void shouldOnlyIncludeActiveContractsInInvoice() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        Contract activeContract = mock(Contract.class);
        when(activeContract.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(activeContract.getProductId()).thenReturn(testProduct.getId());
        when(activeContract.getId()).thenReturn(UUID.randomUUID());
        when(activeContract.getCreationDate()).thenReturn(LocalDate.now());

        Contract inactiveContract = mock(Contract.class);
        when(inactiveContract.getStatus()).thenReturn(ContractStatus.INACTIVE);

        when(contractService.findContractsByCustomerId(customerId))
                .thenReturn(List.of(activeContract, inactiveContract));
        when(productService.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Invoice invoice = billingService.generateInvoice(customerId);

        assertEquals(1, invoice.items().size());
        assertEquals(testProduct.getId(), invoice.items().get(0).productId());
    }

    @Test
    @DisplayName("Generation with multiple contracts for the same product")
    void shouldIncludeMultipleInvoiceItemsWhenContractsAreForSameProduct() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        UUID c1Id = UUID.randomUUID();
        UUID c2Id = UUID.randomUUID();

        Contract c1 = mock(Contract.class);
        when(c1.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(c1.getProductId()).thenReturn(testProduct.getId());
        when(c1.getId()).thenReturn(c1Id);
        when(c1.getCreationDate()).thenReturn(LocalDate.now());

        Contract c2 = mock(Contract.class);
        when(c2.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(c2.getProductId()).thenReturn(testProduct.getId());
        when(c2.getId()).thenReturn(c2Id);
        when(c2.getCreationDate()).thenReturn(LocalDate.now());

        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of(c1, c2));
        when(productService.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Invoice invoice = billingService.generateInvoice(customerId);

        assertEquals(2, invoice.items().size());
        assertEquals(c1Id, invoice.items().get(0).contractId());
        assertEquals(c2Id, invoice.items().get(1).contractId());
    }

    @Test
    @DisplayName("Stress test for generation with 100 active contracts")
    void shouldHandleLargeNumberOfContractsWhenGeneratingInvoice() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        int count = 100;
        List<Contract> contracts = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Contract c = mock(Contract.class);
            when(c.getStatus()).thenReturn(ContractStatus.ACTIVE);
            when(c.getProductId()).thenReturn(testProduct.getId());
            when(c.getId()).thenReturn(UUID.randomUUID());
            when(c.getCreationDate()).thenReturn(LocalDate.now());
            contracts.add(c);
        }

        when(contractService.findContractsByCustomerId(customerId)).thenReturn(contracts);
        when(productService.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));

        Invoice invoice = billingService.generateInvoice(customerId);

        assertEquals(count, invoice.items().size());
        BigDecimal expectedTotal = testProduct.getSetupFee().add(testProduct.getMonthlyFee())
                .multiply(new BigDecimal(count));
        assertEquals(expectedTotal, invoice.totalAmount());
    }
}
