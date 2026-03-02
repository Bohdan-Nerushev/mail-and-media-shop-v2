package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ContractRepository Tests")
class ContractRepositoryTest {

    private ContractRepository contractRepository;

    private Customer testCustomer;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        contractRepository = new ContractRepositoryImpl();

        testCustomer = mock(Customer.class);
        when(testCustomer.status()).thenReturn(CustomerStatus.ACTIVE);
        when(testCustomer.brand()).thenReturn(Brand.WEB_DE);
        when(testCustomer.id()).thenReturn(UUID.randomUUID());

        testProduct = new StandardMailProduct(
                "Test Product",
                Brand.WEB_DE,
                new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Success: save stores a contract and returns it")
    void shouldStoreContractCorrectlyWhenSaveIsCalled() throws BrandMismatchException {
        Contract contract = Contract.create(testCustomer, testProduct);

        Contract saved = contractRepository.save(contract);

        assertNotNull(saved);
        assertEquals(contract.id(), saved.id());

        Optional<Contract> found = contractRepository.findById(contract.id());
        assertTrue(found.isPresent());
        assertEquals(contract, found.get());
    }

    @Test
    @DisplayName("Success: update modifies existing contract")
    void shouldModifyExistingContractWhenUpdateIsCalled() throws BrandMismatchException {
        Contract contract = Contract.create(testCustomer, testProduct);
        contractRepository.save(contract);
        Contract updatedInstance = contract.withStatus(ContractStatus.ACTIVE);

        Contract updated = contractRepository.update(updatedInstance);

        assertEquals(ContractStatus.ACTIVE, updated.status());
        assertEquals(ContractStatus.ACTIVE, contractRepository.findById(contract.id()).get().status());
    }

    @Test
    @DisplayName("Success: findByCustomerId returns all contracts for customer")
    void shouldReturnAllContractsByCustomerIdWhenCustomerHasThem() throws BrandMismatchException {
        UUID customerId = testCustomer.id();
        Contract contract1 = Contract.create(testCustomer, testProduct);
        Contract contract2 = Contract.create(testCustomer, testProduct);
        contractRepository.save(contract1);
        contractRepository.save(contract2);

        List<Contract> results = contractRepository.findByCustomerId(customerId);

        assertTrue(results.contains(contract1));
        assertTrue(results.contains(contract2));
    }

    @Test
    @DisplayName("Success: findByProductId returns all contracts for product")
    void shouldReturnAllContractsByProductIdWhenProductHasThem() throws BrandMismatchException {
        UUID productId = testProduct.getId();
        Contract contract = Contract.create(testCustomer, testProduct);
        contractRepository.save(contract);

        List<Contract> results = contractRepository.findByProductId(productId);

        assertTrue(results.stream().anyMatch(c -> c.productId().equals(productId)));
    }

    @Test
    @DisplayName("Boundary: findById returns empty Optional for non-existent ID")
    void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
        Optional<Contract> result = contractRepository.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @DisplayName("Negative: save and update throw exception on null")
    @ParameterizedTest
    @NullSource
    void shouldThrowExceptionWhenSaveOrUpdateWithNullIsCalled(Contract contract) {
        assertThrows(ContractValidationException.class, () -> contractRepository.save(contract));
        assertThrows(ContractValidationException.class, () -> contractRepository.update(contract));
    }

    @Test
    @DisplayName("Boundary: findAll returns list containing saved contracts")
    void shouldReturnListContainingAllSavedContracts() throws BrandMismatchException {
        Contract contract = Contract.create(testCustomer, testProduct);
        contractRepository.save(contract);

        List<Contract> all = contractRepository.findAll();

        assertTrue(all.size() >= 1);
        assertTrue(all.stream().anyMatch(c -> c.id().equals(contract.id())));
    }

    @Test
    @DisplayName("Negative: search by null IDs throws exception")
    void shouldThrowExceptionWhenSearchingByNullId() {
        assertThrows(ContractValidationException.class, () -> contractRepository.findById(null));
        assertThrows(ContractValidationException.class, () -> contractRepository.findByCustomerId(null));
        assertThrows(ContractValidationException.class, () -> contractRepository.findByProductId(null));
    }

    @Test
    @DisplayName("Boundary: findByCustomerId returns empty list when no match found")
    void shouldReturnEmptyListWhenNoContractsExistsForCustomerId() {
        List<Contract> results = contractRepository.findByCustomerId(UUID.randomUUID());
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Boundary: findByProductId returns empty list when no match found")
    void shouldReturnEmptyListWhenNoContractsExistsForProductId() {
        List<Contract> results = contractRepository.findByProductId(UUID.randomUUID());
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Success: findByCustomerId filters correct contracts among many")
    void shouldFilterCorrectContractsAmongManyByCustomerId() throws BrandMismatchException {
        Customer anotherCustomer = mock(Customer.class);
        when(anotherCustomer.id()).thenReturn(UUID.randomUUID());
        when(anotherCustomer.status()).thenReturn(CustomerStatus.ACTIVE);
        when(anotherCustomer.brand()).thenReturn(Brand.WEB_DE);

        Contract target1 = Contract.create(testCustomer, testProduct);
        Contract target2 = Contract.create(testCustomer, testProduct);
        Contract other = Contract.create(anotherCustomer, testProduct);

        contractRepository.save(target1);
        contractRepository.save(target2);
        contractRepository.save(other);

        List<Contract> results = contractRepository.findByCustomerId(testCustomer.id());

        assertEquals(2, results.size());
        assertTrue(results.contains(target1));
        assertTrue(results.contains(target2));
        assertTrue(results.stream().noneMatch(c -> c.customerId().equals(anotherCustomer.id())));
    }
}
