package dev.mam.buizsol.mamshop.contract.service;

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

class ContractRepositoryTest {

    private ContractRepository contractRepository;

    private Customer testCustomer;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        contractRepository = ContractRepositoryImpl.getInstance();

        testCustomer = mock(Customer.class);
        when(testCustomer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(testCustomer.getBrand()).thenReturn(Brand.WEB_DE);
        when(testCustomer.getId()).thenReturn(UUID.randomUUID());

        testProduct = new StandardMailProduct(
                "Test Product",
                Brand.WEB_DE,
                new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("01. Success: save stores a contract and returns it")
    void test01_save_Success() {
        Contract contract = new Contract(testCustomer, testProduct);

        Contract saved = contractRepository.save(contract);

        assertNotNull(saved);
        assertEquals(contract.getId(), saved.getId());

        Optional<Contract> found = contractRepository.findById(contract.getId());
        assertTrue(found.isPresent());
        assertEquals(contract, found.get());
    }

    @Test
    @DisplayName("02. Success: update modifies existing contract")
    void test02_update_Success() {
        Contract contract = new Contract(testCustomer, testProduct);
        contractRepository.save(contract);
        contract.updateStatus(ContractStatus.INACTIVE);

        Contract updated = contractRepository.update(contract);

        assertEquals(ContractStatus.INACTIVE, updated.getStatus());
        assertEquals(ContractStatus.INACTIVE, contractRepository.findById(contract.getId()).get().getStatus());
    }

    @Test
    @DisplayName("03. Success: findByCustomerId returns all contracts for customer")
    void test03_findByCustomerId_Success() {
        UUID customerId = testCustomer.getId();
        Contract contract1 = new Contract(testCustomer, testProduct);
        Contract contract2 = new Contract(testCustomer, testProduct);
        contractRepository.save(contract1);
        contractRepository.save(contract2);

        List<Contract> results = contractRepository.findByCustomerId(customerId);

        assertTrue(results.contains(contract1));
        assertTrue(results.contains(contract2));
    }

    @Test
    @DisplayName("04. Success: findByProductId returns all contracts for product")
    void test04_findByProductId_Success() {
        UUID productId = testProduct.getId();
        Contract contract = new Contract(testCustomer, testProduct);
        contractRepository.save(contract);

        List<Contract> results = contractRepository.findByProductId(productId);

        assertTrue(results.stream().anyMatch(c -> c.getProductId().equals(productId)));
    }

    @Test
    @DisplayName("05. Boundary: findById returns empty Optional for non-existent ID")
    void test05_findById_NotFound() {
        Optional<Contract> result = contractRepository.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("06. Negative: save and update throw exception on null")
    void test06_saveAndUpdate_NullParam(Contract contract) {
        assertThrows(ContractValidationException.class, () -> contractRepository.save(contract));
        assertThrows(ContractValidationException.class, () -> contractRepository.update(contract));
    }

    @Test
    @DisplayName("07. Boundary: findAll returns list containing saved contracts")
    void test07_findAll_Success() {
        Contract contract = new Contract(testCustomer, testProduct);
        contractRepository.save(contract);

        List<Contract> all = contractRepository.findAll();

        assertTrue(all.size() >= 1);
        assertTrue(all.stream().anyMatch(c -> c.getId().equals(contract.getId())));
    }

    @Test
    @DisplayName("08. Negative: search by null IDs throws exception")
    void test08_searchMethods_NullParam() {
        assertThrows(ContractValidationException.class, () -> contractRepository.findById(null));
        assertThrows(ContractValidationException.class, () -> contractRepository.findByCustomerId(null));
        assertThrows(ContractValidationException.class, () -> contractRepository.findByProductId(null));
    }

    @Test
    @DisplayName("09. Boundary: findByCustomerId returns empty list when no match found")
    void test09_findByCustomerId_Empty() {
        List<Contract> results = contractRepository.findByCustomerId(UUID.randomUUID());
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("10. Boundary: findByProductId returns empty list when no match found")
    void test10_findByProductId_Empty() {
        List<Contract> results = contractRepository.findByProductId(UUID.randomUUID());
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("11. Success: findByCustomerId filters correct contracts among many")
    void test11_findByCustomerId_ComplexFiltering() {
        Customer anotherCustomer = mock(Customer.class);
        when(anotherCustomer.getId()).thenReturn(UUID.randomUUID());
        when(anotherCustomer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(anotherCustomer.getBrand()).thenReturn(Brand.WEB_DE);

        Contract target1 = new Contract(testCustomer, testProduct);
        Contract target2 = new Contract(testCustomer, testProduct);
        Contract other = new Contract(anotherCustomer, testProduct);

        contractRepository.save(target1);
        contractRepository.save(target2);
        contractRepository.save(other);

        List<Contract> results = contractRepository.findByCustomerId(testCustomer.getId());

        assertEquals(2, results.size());
        assertTrue(results.contains(target1));
        assertTrue(results.contains(target2));
        assertTrue(results.stream().noneMatch(c -> c.getCustomerId().equals(anotherCustomer.getId())));
    }

    @Test
    @DisplayName("12. Success: verify ContractRepository is a Singleton")
    void test12_singleton_Identity() {
        ContractRepository instance1 = ContractRepository.getInstance();
        ContractRepository instance2 = ContractRepository.getInstance();

        assertEquals(instance1, instance2);
    }
}
