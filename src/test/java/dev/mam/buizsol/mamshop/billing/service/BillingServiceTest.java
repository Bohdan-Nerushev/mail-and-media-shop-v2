package dev.mam.buizsol.mamshop.billing.service;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidateException;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    @DisplayName("01: Successful generation of invoice without discount")
    void test1_generateInvoiceWithoutDiscount_success() throws Exception {
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
        assertEquals(customerId, invoice.getCustomerId());
        assertEquals(BigDecimal.ZERO, invoice.getDiscount());
        assertEquals(1, invoice.getItems().size());
    }

    @Test
    @DisplayName("02: Successful generation of invoice with discount")
    void test2_generateInvoiceWithDiscount_success() throws Exception {
        BigDecimal discount = new BigDecimal("1.00");
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));
        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of());

        Invoice invoice = billingService.generateInvoice(customerId, discount);

        assertNotNull(invoice);
        assertEquals(discount, invoice.getDiscount());
    }

    @Test
    @DisplayName("03: Verification of correct totals calculation in invoice")
    void test3_generateInvoice_correctCalculations() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        Product p1 = new StandardMailProduct("P1", Brand.GMX, new BigDecimal("5.00")); // Setup 4.99
        Product p2 = new StandardMailProduct("P2", Brand.GMX, new BigDecimal("15.00")); // Setup 4.99

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

        assertEquals(new BigDecimal("9.98"), invoice.getTotalSetupFee());
        assertEquals(new BigDecimal("20.00"), invoice.getTotalMonthlyFee());
        assertEquals(new BigDecimal("24.98"), invoice.getTotalAmount());
    }

    @Test
    @DisplayName("04: Verification of invoice item field structure")
    void test4_generateInvoice_invoiceItemStructure() throws Exception {
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
        InvoiceItem item = invoice.getItems().get(0);

        assertEquals(testProduct.getId(), item.getProductId());
        assertEquals(testProduct.getName(), item.getProductName());
        assertEquals(contract.getId(), item.getContractId());
        assertEquals(creationDate, item.getContractCreationDate());
        assertEquals(testProduct.getSetupFee(), item.getSetupFee());
        assertEquals(testProduct.getMonthlyFee(), item.getMonthlyFee());
    }

    @ParameterizedTest(name = "Test 5: Invalid discount validation - value: {0}")
    @DisplayName("05: Invalid discount validation checks")
    @ValueSource(strings = { "0.01", "0.05", "0.10", "-0.01", "-1.00" })
    void test5_generateInvoice_invalidDiscount_throwsException(BigDecimal invalidDiscount) {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        assertThrows(InvalidInvoiceDiscountException.class,
                () -> billingService.generateInvoice(customerId, invalidDiscount));
    }

    @Test
    @DisplayName("06: Handling scenario when customer is not found")
    void test6_generateInvoice_customerNotFound_throwsException() {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> billingService.generateInvoice(customerId));
    }

    @Test
    @DisplayName("07: Successful generation for customer without active contracts")
    void test7_generateInvoice_noActiveContracts_emptyInvoice() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        Contract contract = mock(Contract.class);
        when(contract.getStatus()).thenReturn(ContractStatus.INACTIVE);
        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of(contract));

        Invoice invoice = billingService.generateInvoice(customerId);

        assertTrue(invoice.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, invoice.getTotalSetupFee());
        assertEquals(BigDecimal.ZERO, invoice.getTotalMonthlyFee());
        assertEquals(BigDecimal.ZERO, invoice.getTotalAmount());
    }

    @ParameterizedTest(name = "Test 8: Null arguments validation - customerId={0}, discount={1}")
    @DisplayName("08: Null arguments validation for generateInvoice")
    @CsvSource(value = {
            "null, 10.00",
            "550e8400-e29b-41d4-a716-446655440000, null",
            "null, null"
    }, nullValues = { "null" })
    void test8_generateInvoice_nullArguments_throwsException(UUID cid, BigDecimal disc) {
        assertThrows(InvoiceValidateException.class, () -> billingService.generateInvoice(cid, disc));
    }

    @Test
    @DisplayName("09: Handling scenario when product is not found for a contract")
    void test9_generateInvoice_productNotFound_throwsException() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));
        Contract contract = mock(Contract.class);
        when(contract.getStatus()).thenReturn(ContractStatus.ACTIVE);
        when(contract.getProductId()).thenReturn(UUID.randomUUID());
        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of(contract));
        when(productService.findById(any())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> billingService.generateInvoice(customerId));
    }

    @ParameterizedTest(name = "Test 10: Boundary discount values - value: {0}")
    @DisplayName("10: Verification of valid boundary discount values")
    @ValueSource(strings = { "0.00", "0.11", "0.10001", "1.00", "5.00", "100.00" })
    void test10_generateInvoice_validDiscounts_success(BigDecimal validDiscount) throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));
        when(contractService.findContractsByCustomerId(customerId)).thenReturn(List.of());

        Invoice invoice = billingService.generateInvoice(customerId, validDiscount);

        assertNotNull(invoice);
        assertEquals(validDiscount, invoice.getDiscount());
    }

    @Test
    @DisplayName("11: Generation with mixed contract states (active and inactive)")
    void test11_generateInvoice_mixedContractStates() throws Exception {
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

        assertEquals(1, invoice.getItems().size());
        assertEquals(testProduct.getId(), invoice.getItems().get(0).getProductId());
    }

    @Test
    @DisplayName("12: Generation with multiple contracts for the same product")
    void test12_generateInvoice_duplicateProducts() throws Exception {
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

        assertEquals(2, invoice.getItems().size());
        assertEquals(c1Id, invoice.getItems().get(0).getContractId());
        assertEquals(c2Id, invoice.getItems().get(1).getContractId());
    }

    @Test
    @DisplayName("13: Stress test for generation with 100 active contracts")
    void test13_generateInvoice_stressLogic() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(testCustomer));

        int count = 100;
        java.util.List<Contract> contracts = new java.util.ArrayList<>(count);
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

        assertEquals(count, invoice.getItems().size());
        BigDecimal expectedTotal = testProduct.getSetupFee().add(testProduct.getMonthlyFee())
                .multiply(new BigDecimal(count));
        assertEquals(expectedTotal, invoice.getTotalAmount());
    }
}
