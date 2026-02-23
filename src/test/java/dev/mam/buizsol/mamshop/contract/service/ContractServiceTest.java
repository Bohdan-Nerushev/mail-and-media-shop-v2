package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractService Tests")
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

        matchingProduct = new StandardMailProduct(
                "Test Product",
                Brand.WEB_DE,
                new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Success: createContract generates and saves a contract")
    void shouldGenerateAndSaveContractWhenCustomerIsActiveAndDataIsValid() {
        setupActiveCustomer();
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract created = contractService.createContract(activeCustomer, matchingProduct);

        assertNotNull(created);
        assertEquals(activeCustomer.id(), created.customerId());
        assertEquals(matchingProduct.getId(), created.productId());
        assertEquals(ContractStatus.INACTIVE, created.status());
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    @DisplayName("Success: findContractById returns contract when present")
    void shouldReturnContractWhenIdExists() {
        UUID contractId = UUID.randomUUID();
        Contract contract = mock(Contract.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

        Optional<Contract> result = contractService.findContractById(contractId);

        assertTrue(result.isPresent());
        assertEquals(contract, result.get());
    }

    @Test
    @DisplayName("Success: findContractsByCustomerId returns list of contracts")
    void shouldReturnListOfContractsWhenCustomerHasThem() {
        UUID customerId = UUID.randomUUID();
        List<Contract> contracts = List.of(mock(Contract.class), mock(Contract.class));
        when(contractRepository.findByCustomerId(customerId)).thenReturn(contracts);

        List<Contract> result = contractService.findContractsByCustomerId(customerId);

        assertEquals(2, result.size());
        assertEquals(contracts, result);
    }

    @Test
    @DisplayName("Success: updateContractStatus updates status correctly")
    void shouldUpdateStatusWhenContractExists() throws ContractNotFoundException {
        setupActiveCustomer();
        UUID contractId = UUID.randomUUID();
        Contract contract = Contract.create(activeCustomer, matchingProduct);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractRepository.update(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract updated = contractService.updateContractStatus(contractId, ContractStatus.INACTIVE);

        assertEquals(ContractStatus.INACTIVE, updated.status());
        verify(contractRepository).update(contract);
    }

    @Test
    @DisplayName("Negative: updateContractStatus throws exception when contract not found")
    void shouldThrowExceptionWhenUpdatingStatusOfNonExistentContract() {
        UUID contractId = UUID.randomUUID();
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        assertThrows(ContractNotFoundException.class,
                () -> contractService.updateContractStatus(contractId, ContractStatus.INACTIVE));
    }

    @DisplayName("Boundary/Negative: createContract throws exception on null parameters")
    @ParameterizedTest
    @CsvSource({
            "true, false",
            "false, true",
            "true, true"
    })
    void shouldThrowExceptionWhenCreatingContractWithNullParameters(boolean customerIsNull, boolean productIsNull) {
        Customer customer = customerIsNull ? null : activeCustomer;
        Product product = productIsNull ? null : matchingProduct;
        assertThrows(ContractValidationException.class, () -> contractService.createContract(customer, product));
    }

    @Test
    @DisplayName("Negative: createContract throws exception on brand mismatch")
    void shouldThrowExceptionWhenCreatingContractWithBrandMismatch() {
        when(activeCustomer.brand()).thenReturn(Brand.WEB_DE);
        Product mismatchProduct = new StandardMailProduct(
                "Mismatch Brand",
                Brand.GMX,
                new BigDecimal("10.00"));

        assertThrows(BrandMismatchException.class,
                () -> contractService.createContract(activeCustomer, mismatchProduct));
    }

    @Test
    @DisplayName("Negative: createContract throws exception when customer is inactive")
    void shouldThrowExceptionWhenCreatingContractForInactiveCustomer() {
        Customer inactiveCustomer = mock(Customer.class);
        when(inactiveCustomer.status()).thenReturn(CustomerStatus.INACTIVE);
        when(inactiveCustomer.brand()).thenReturn(Brand.WEB_DE);

        assertThrows(CustomerNotActiveException.class,
                () -> contractService.createContract(inactiveCustomer, matchingProduct));
    }

    @Test
    @DisplayName("Boundary: findContractsByCustomerId returns empty list when no contracts exist")
    void shouldReturnEmptyListWhenNoContractsForCustomer() {
        UUID customerId = UUID.randomUUID();
        when(contractRepository.findByCustomerId(customerId)).thenReturn(List.of());

        List<Contract> result = contractService.findContractsByCustomerId(customerId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Success: findContractsByProductId returns list of contracts")
    void shouldReturnListOfContractsWhenProductHasThem() {
        UUID productId = UUID.randomUUID();
        List<Contract> contracts = List.of(mock(Contract.class));
        when(contractRepository.findByProductId(productId)).thenReturn(contracts);

        List<Contract> result = contractService.findContractsByProductId(productId);

        assertEquals(1, result.size());
        assertEquals(contracts, result);
    }

    @Test
    @DisplayName("Boundary: findContractsByProductId returns empty list when no contracts exist")
    void shouldReturnEmptyListWhenNoContractsForProduct() {
        UUID productId = UUID.randomUUID();
        when(contractRepository.findByProductId(productId)).thenReturn(List.of());

        List<Contract> result = contractService.findContractsByProductId(productId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Success: update status when status is already the same")
    void shouldUpdateStatusSuccessfullyWhenStatusIsTheSame() throws ContractNotFoundException {
        setupActiveCustomer();
        UUID contractId = UUID.randomUUID();
        Contract contract = Contract.create(activeCustomer, matchingProduct).withStatus(ContractStatus.ACTIVE);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractRepository.update(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract updated = contractService.updateContractStatus(contractId, ContractStatus.ACTIVE);

        assertEquals(ContractStatus.ACTIVE, updated.status());
        verify(contractRepository).update(any(Contract.class));
    }

    @Test
    @DisplayName("Success: handle multiple consecutive status updates")
    void shouldHandleMultipleConsecutiveStatusUpdates() throws ContractNotFoundException {
        setupActiveCustomer();
        UUID contractId = UUID.randomUUID();
        Contract contract = Contract.create(activeCustomer, matchingProduct);

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractRepository.update(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract updated1 = contractService.updateContractStatus(contractId, ContractStatus.ACTIVE);
        assertEquals(ContractStatus.ACTIVE, updated1.status());

        Contract updated2 = contractService.updateContractStatus(contractId, ContractStatus.INACTIVE);
        assertEquals(ContractStatus.INACTIVE, updated2.status());

        Contract updated3 = contractService.updateContractStatus(contractId, ContractStatus.ACTIVE);
        assertEquals(ContractStatus.ACTIVE, updated3.status());

        verify(contractRepository, times(3)).update(any(Contract.class));
    }

    @Test
    @DisplayName("Boundary: contract creation date should always be current date")
    void shouldSetCreationDateToCurrentDateWhenCreatingContract() {
        setupActiveCustomer();
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract created = contractService.createContract(activeCustomer, matchingProduct);

        assertEquals(LocalDate.now(), created.creationDate());
        verify(contractRepository).save(any(Contract.class));
    }

    private void setupActiveCustomer() {
        when(activeCustomer.status()).thenReturn(CustomerStatus.ACTIVE);
        when(activeCustomer.brand()).thenReturn(Brand.WEB_DE);
        when(activeCustomer.id()).thenReturn(UUID.randomUUID());
    }
}
