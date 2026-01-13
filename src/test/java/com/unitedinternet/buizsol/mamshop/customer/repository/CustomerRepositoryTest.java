package com.unitedinternet.buizsol.mamshop.customer.repository;

import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import com.unitedinternet.buizsol.mamshop.customer.model.Address;
import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.customer.model.CommunicationDetails;
import com.unitedinternet.buizsol.mamshop.customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerRepositoryTest {

    private CustomerRepository repository;

    @BeforeEach
    void setUp() {
        repository = CustomerRepository.getInstance();
        cleanStorage();
    }

    @Test
    @DisplayName("01: Should successfully save and retrieve a single customer by its identifier")
    void test01_saveAndRetrieveCustomer() {
        final Customer customer = createTestCustomer("John", "Doe", Brand.GMX);
        repository.save(customer);

        final Optional<Customer> found = repository.findById(customer.getId());

        assertTrue(found.isPresent());
        assertEquals(customer.getId(), found.get().getId());
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("02: Should find all customers even when they belong to different brands")
    void test02_findAllWithDifferentBrands(final Brand brand) {
        final Customer customer = createTestCustomer("FirstName", "LastName", brand);
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
        final Customer customer = createTestCustomer("John", "Doe", Brand.WEB_DE);
        repository.save(customer);

        repository.update(customer);

        final Optional<Customer> found = repository.findById(customer.getId());
        assertTrue(found.isPresent());
    }

    @Test
    @DisplayName("05: Should successfully delete an existing customer from storage")
    void test05_deleteExistingCustomer() throws CustomerNotFoundException {
        final Customer customer = createTestCustomer("John", "Doe", Brand.GMX);
        repository.save(customer);

        repository.delete(customer.getId());

        final Optional<Customer> found = repository.findById(customer.getId());
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
        final Customer customer = createTestCustomer("Ghost", "User", Brand.MAIL_COM);
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
    @ValueSource(ints = { 1, 5, 50 })
    @DisplayName("11: Should handle multiple concurrent-like entries and maintain correct size (Boundary)")
    void test11_multipleEntriesSize(final int count) {
        cleanStorage(); // Ensure fresh state for count test
        for (int i = 0; i < count; i++) {
            repository.save(createTestCustomer("User", "Nr" + i, Brand.WEB_DE));
        }
        assertEquals(count, repository.findAll().size());
    }

    private void cleanStorage() {
        repository.findAll().forEach(customer -> {
            try {
                repository.delete(customer.getId());
            } catch (CustomerNotFoundException e) {
            }
        });
    }

    private Customer createTestCustomer(
            final String firstName,
            final String lastName,
            final Brand brand) {
        final Address address = new Address("Street", "1", "12345", "City", "Country");
        final CommunicationDetails details = new CommunicationDetails("test@email.com", "987654321");
        return new Customer(firstName, lastName, LocalDate.of(1985, 5, 20), address, null, details, brand);
    }
}
