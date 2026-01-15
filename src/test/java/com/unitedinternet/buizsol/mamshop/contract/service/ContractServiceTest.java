package com.unitedinternet.buizsol.mamshop.contract.service;

import com.unitedinternet.buizsol.mamshop.contract.exception.ContractNotFoundException;
import com.unitedinternet.buizsol.mamshop.contract.model.Contract;
import com.unitedinternet.buizsol.mamshop.contract.model.ContractStatus;
import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.customer.model.Customer;
import com.unitedinternet.buizsol.mamshop.customer.model.CustomerStatus;
import com.unitedinternet.buizsol.mamshop.product.model.Product;
import com.unitedinternet.buizsol.mamshop.product.model.StandardMailProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Customer activeCustomer;
    private Product matchingProduct;

    @BeforeEach
    void setUp() {
        activeCustomer = mock(Customer.class);
        lenient().when(activeCustomer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        lenient().when(activeCustomer.getBrand()).thenReturn(Brand.WEB_DE);
        lenient().when(activeCustomer.getId()).thenReturn(UUID.randomUUID());

        matchingProduct = new StandardMailProduct(
                "Test Product",
                Brand.WEB_DE,
                new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("01. Success: createContract generates and saves a contract")
    void test01_createContract_Success() {
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract created = contractService.createContract(activeCustomer, matchingProduct);

        assertNotNull(created);
        assertEquals(activeCustomer.getId(), created.getCustomerId());
        assertEquals(matchingProduct.getId(), created.getProductId());
        assertEquals(ContractStatus.ACTIVE, created.getStatus());
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    @DisplayName("02. Success: findContractById returns contract when present")
    void test02_findContractById_Success() {
        UUID contractId = UUID.randomUUID();
        Contract contract = mock(Contract.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

        Optional<Contract> result = contractService.findContractById(contractId);

        assertTrue(result.isPresent());
        assertEquals(contract, result.get());
    }

    @Test
    @DisplayName("03. Success: findContractsByCustomerId returns list of contracts")
    void test03_findContractsByCustomerId_Success() {
        UUID customerId = UUID.randomUUID();
        List<Contract> contracts = List.of(mock(Contract.class), mock(Contract.class));
        when(contractRepository.findByCustomerId(customerId)).thenReturn(contracts);

        List<Contract> result = contractService.findContractsByCustomerId(customerId);

        assertEquals(2, result.size());
        assertEquals(contracts, result);
    }

    @Test
    @DisplayName("04. Success: updateContractStatus updates status correctly")
    void test04_updateContractStatus_Success() throws ContractNotFoundException {
        UUID contractId = UUID.randomUUID();
        Contract contract = new Contract(activeCustomer, matchingProduct);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractRepository.update(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract updated = contractService.updateContractStatus(contractId, ContractStatus.INACTIVE);

        assertEquals(ContractStatus.INACTIVE, updated.getStatus());
        verify(contractRepository).update(contract);
    }

    @Test
    @DisplayName("05. Negative: updateContractStatus throws exception when contract not found")
    void test05_updateContractStatus_NotFound() {
        UUID contractId = UUID.randomUUID();
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        assertThrows(ContractNotFoundException.class,
                () -> contractService.updateContractStatus(contractId, ContractStatus.INACTIVE));
    }

    @ParameterizedTest
    @CsvSource({
            "true, false",
            "false, true",
            "true, true"
    })
    @DisplayName("06. Boundary/Negative: createContract throws exception on null parameters")
    void test06_createContract_NullParams(boolean customerIsNull, boolean productIsNull) {
        Customer customer = customerIsNull ? null : activeCustomer;
        Product product = productIsNull ? null : matchingProduct;
        assertThrows(IllegalArgumentException.class, () -> contractService.createContract(customer, product));
    }

    @Test
    @DisplayName("07. Negative: createContract throws exception on brand mismatch")
    void test07_createContract_BrandMismatch() {
        Product mismatchProduct = new StandardMailProduct(
                "Mismatch Brand",
                Brand.GMX,
                new BigDecimal("10.00"));

        assertThrows(RuntimeException.class, () -> contractService.createContract(activeCustomer, mismatchProduct));
    }

    @Test
    @DisplayName("08. Negative: createContract throws exception when customer is inactive")
    void test08_createContract_CustomerInactive() {
        Customer inactiveCustomer = mock(Customer.class);
        when(inactiveCustomer.getStatus()).thenReturn(CustomerStatus.INACTIVE);
        lenient().when(inactiveCustomer.getBrand()).thenReturn(Brand.WEB_DE);

        assertThrows(RuntimeException.class, () -> contractService.createContract(inactiveCustomer, matchingProduct));
    }

    @ParameterizedTest
    @CsvSource(value = {
            ", ACTIVE",
            "550e8400-e29b-41df-a447-4462db01c001, ",
            ", "
    }, nullValues = { "" })
    @DisplayName("09. Boundary/Negative: updateContractStatus throws exception on null parameters")
    void test09_updateContractStatus_NullParams(UUID id, ContractStatus status) {
        assertThrows(IllegalArgumentException.class, () -> contractService.updateContractStatus(id, status));
    }

    @Test
    @DisplayName("10. Boundary: findContractsByCustomerId returns empty list when no contracts exist")
    void test10_findContractsByCustomerId_Empty() {
        UUID customerId = UUID.randomUUID();
        when(contractRepository.findByCustomerId(customerId)).thenReturn(List.of());

        List<Contract> result = contractService.findContractsByCustomerId(customerId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("11. Success: findContractsByProductId returns list of contracts")
    void test11_findContractsByProductId_Success() {
        UUID productId = UUID.randomUUID();
        List<Contract> contracts = List.of(mock(Contract.class));
        when(contractRepository.findByProductId(productId)).thenReturn(contracts);

        List<Contract> result = contractService.findContractsByProductId(productId);

        assertEquals(1, result.size());
        assertEquals(contracts, result);
    }

    @Test
    @DisplayName("12. Boundary: findContractsByProductId returns empty list when no contracts exist")
    void test12_findContractsByProductId_Empty() {
        UUID productId = UUID.randomUUID();
        when(contractRepository.findByProductId(productId)).thenReturn(List.of());

        List<Contract> result = contractService.findContractsByProductId(productId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("13. Success: verify ContractService is a Singleton")
    void test13_singleton_Identity() {
        ContractService instance1 = ContractService.getInstance();
        ContractService instance2 = ContractService.getInstance();

        assertEquals(instance1, instance2);
    }
}
