package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.Customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {

    @Mock
    private Customer customer;

    private CustomerRepository repository;

    @BeforeEach
    void setUp() {
        repository = CustomerRepository.getInstance();
        if (customer != null) {
            clearInvocations(customer);
        }
    }

    @AfterEach
    public void cleanStorage() {
        repository.findAll().forEach(c -> {
            try {
                repository.delete(c.getId());
            } catch (final CustomerNotFoundException e) {
            }
        });
    }

    @Test
    @DisplayName("01: Should successfully save and retrieve a single customer by its identifier")
    void test01_saveAndRetrieveCustomer() {
        final UUID customerId = UUID.randomUUID();
        when(customer.getId()).thenReturn(customerId);

        repository.save(customer);

        final Optional<Customer> found = repository.findById(customerId);

        assertTrue(found.isPresent());
        assertEquals(customer, found.get());
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("02: Should find all customers even when they belong to different brands")
    void test02_findAllWithDifferentBrands(final Brand brand) {
        final UUID customerId = UUID.randomUUID();
        when(customer.getId()).thenReturn(customerId);
        when(customer.getBrand()).thenReturn(brand);

        repository.save(customer);

        final Collection<Customer> all = repository.findAll();

        assertTrue(all.stream().anyMatch(c -> c.getBrand() == brand));
    }

    @Test
    @DisplayName("03: Should return empty optional when attempting to find a customer by a non-existent identifier")
    void test03_findNonExistentReturnsEmpty() {
        final Optional<Customer> found = repository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("04: Should successfully update existing customer data in storage")
    void test04_updateExistingCustomer() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        when(customer.getId()).thenReturn(customerId);

        repository.save(customer);
        repository.update(customer);

        final Optional<Customer> found = repository.findById(customerId);
        assertTrue(found.isPresent());
        assertEquals(customer, found.get());
    }

    @Test
    @DisplayName("05: Should successfully delete an existing customer from storage")
    void test05_deleteExistingCustomer() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        when(customer.getId()).thenReturn(customerId);

        repository.save(customer);
        repository.delete(customerId);

        final Optional<Customer> found = repository.findById(customerId);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("06: Should throw CustomerNotFoundException when attempting to delete a non-existent customer")
    void test06_throwExceptionOnDeleteNonExistent() {
        final UUID nonExistentId = UUID.randomUUID();
        assertThrows(CustomerNotFoundException.class, () -> repository.delete(nonExistentId));
    }

    @Test
    @DisplayName("07: Should throw CustomerNotFoundException when attempting to update a non-existent customer")
    void test07_throwExceptionOnUpdateNonExistent() {
        when(customer.getId()).thenReturn(UUID.randomUUID());
        assertThrows(CustomerNotFoundException.class, () -> repository.update(customer));
    }

    @Test
    @DisplayName("08: Should throw IllegalArgumentException when passing null to save method (Negative)")
    void test08_throwExceptionOnSaveNull() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    @DisplayName("09: Should throw IllegalArgumentException when searching by null identifier (Negative)")
    void test09_throwExceptionOnFindNullId() {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
    }

    @Test
    @DisplayName("10: Should handle empty storage state returning an empty collection (Boundary)")
    void test10_findAllOnEmptyStorage() {
        final Collection<Customer> all = repository.findAll();
        assertTrue(all.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 5, 20 })
    @DisplayName("11: Should handle multiple concurrent-like entries and maintain correct size (Boundary)")
    void test11_multipleEntriesSize(final int count) {
        for (int i = 0; i < count; i++) {
            final Customer c = mock(Customer.class);
            when(c.getId()).thenReturn(UUID.randomUUID());
            repository.save(c);
        }
        assertEquals(count, repository.findAll().size());
    }
}
