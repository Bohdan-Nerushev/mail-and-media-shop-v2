package com.unitedinternet.buizsol.mamshop.customer.service;

import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import com.unitedinternet.buizsol.mamshop.customer.model.Address;
import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.customer.model.CommunicationDetails;
import com.unitedinternet.buizsol.mamshop.customer.model.Customer;
import com.unitedinternet.buizsol.mamshop.customer.model.CustomerStatus;
import com.unitedinternet.buizsol.mamshop.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository = new InMemoryCustomerRepository();
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    @DisplayName("01: Create customer with valid data should succeed")
    void test01_createCustomer_withValidData_shouldSucceed() {
        final Address address = createDefaultAddress();
        final CommunicationDetails comms = createDefaultCommunicationDetails();

        final Customer customer = customerService.createCustomer(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                address, null, comms, Brand.GMX);

        assertNotNull(customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        assertEquals(1, customerRepository.findAll().size());
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("02: Create customer with different brands should succeed")
    void test02_createCustomer_withDifferentBrands_shouldSucceed(final Brand brand) {
        final Customer customer = customerService.createCustomer(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                createDefaultAddress(), null, createDefaultCommunicationDetails(), brand);

        assertEquals(brand, customer.getBrand());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("03: Create customer with invalid first name should throw exception")
    void test03_createCustomer_withInvalidFirstName_shouldThrowException(final String firstName) {
        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(
                firstName, "Doe", LocalDate.of(1990, 1, 1),
                createDefaultAddress(), null, createDefaultCommunicationDetails(), Brand.WEB_DE));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("04: Create customer with invalid last name should throw exception")
    void test04_createCustomer_withInvalidLastName_shouldThrowException(final String lastName) {
        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(
                "John", lastName, LocalDate.of(1990, 1, 1),
                createDefaultAddress(), null, createDefaultCommunicationDetails(), Brand.WEB_DE));
    }

    @ParameterizedTest
    @CsvSource({
            "1900-01-01, Boundary: very old birth date",
            "2026-01-13, Boundary: today birth date"
    })
    @DisplayName("05: Create customer with boundary birth dates should succeed")
    void test05_createCustomer_withBoundaryBirthDates_shouldSucceed(final String dateStr) {
        final LocalDate birthDate = LocalDate.parse(dateStr);

        final Customer customer = customerService.createCustomer(
                "John", "Doe", birthDate,
                createDefaultAddress(), null, createDefaultCommunicationDetails(), Brand.GMX);

        assertEquals(birthDate, customer.getBirthDate());
    }

    @Test
    @DisplayName("06: Update address should update customer address but keep unique invoice address")
    void test06_updateAddress_shouldUpdateAddressSuccessfully() throws CustomerNotFoundException {
        final Customer customer = createAndSaveDefaultCustomer();
        final Address newAddress = new Address("New St", "2", "67890", "New City", "USA");

        customerService.updateAddress(customer.getId(), newAddress);

        final Customer updated = customerRepository.findById(customer.getId()).orElseThrow();
        assertEquals(newAddress, updated.getAddress());
        assertEquals("Main St", updated.getInvoiceAddress().street());
    }

    @Test
    @DisplayName("07: Update invoice address should update only invoice address")
    void test07_updateInvoiceAddress_shouldUpdateInvoiceAddressSuccessfully() throws CustomerNotFoundException {
        final Customer customer = createAndSaveDefaultCustomer();
        final Address newInvoiceAddress = new Address("Bill St", "3", "11223", "Bill City", "UK");

        customerService.updateInvoiceAddress(customer.getId(), newInvoiceAddress);

        final Customer updated = customerRepository.findById(customer.getId()).orElseThrow();
        assertEquals(newInvoiceAddress, updated.getInvoiceAddress());
        assertEquals("Main St", updated.getAddress().street());
    }

    @Test
    @DisplayName("08: Update communication details should update successfully")
    void test08_updateCommunicationDetails_shouldUpdateSuccessfully() throws CustomerNotFoundException {
        final Customer customer = createAndSaveDefaultCustomer();
        final CommunicationDetails newDetails = new CommunicationDetails("new@test.com", "0987654321");

        customerService.updateCommunicationDetails(customer.getId(), newDetails);

        final Customer updated = customerRepository.findById(customer.getId()).orElseThrow();
        assertEquals(newDetails, updated.getCommunicationDetails());
    }

    @Test
    @DisplayName("09: Activate customer should change status to ACTIVE")
    void test09_activateCustomer_shouldChangeStatusToActive() throws CustomerNotFoundException {

        final Customer customer = createAndSaveDefaultCustomer();

        customerService.activateCustomer(customer.getId());

        final Customer updated = customerRepository.findById(customer.getId()).orElseThrow();
        assertEquals(CustomerStatus.ACTIVE, updated.getStatus());
    }

    @Test
    @DisplayName("10: Deactivate customer should change status to INACTIVE")
    void test10_deactivateCustomer_shouldChangeStatusToInactive() throws CustomerNotFoundException {

        final Customer customer = createAndSaveDefaultCustomer();
        customerService.activateCustomer(customer.getId());

        customerService.deactivateCustomer(customer.getId());

        final Customer updated = customerRepository.findById(customer.getId()).orElseThrow();
        assertEquals(CustomerStatus.INACTIVE, updated.getStatus());
    }

    @Test
    @DisplayName("11: Delete customer should remove customer from repository")
    void test11_deleteCustomer_shouldRemoveFromRepository() throws CustomerNotFoundException {

        final Customer customer = createAndSaveDefaultCustomer();

        customerService.deleteCustomer(customer.getId());

        assertEquals(0, customerRepository.findAll().size());
    }

    @Test
    @DisplayName("12: Operations on non-existent customer should throw CustomerNotFoundException")
    void test12_opsOnNonExistentCustomer_shouldThrowException() {

        final UUID randomId = UUID.randomUUID();

        assertThrows(CustomerNotFoundException.class, () -> customerService.activateCustomer(randomId));
        assertThrows(CustomerNotFoundException.class, () -> customerService.deactivateCustomer(randomId));
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(randomId));
        assertThrows(CustomerNotFoundException.class,
                () -> customerService.updateAddress(randomId, createDefaultAddress()));
    }

    private Customer createAndSaveDefaultCustomer() {
        return customerService.createCustomer(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                createDefaultAddress(), null, createDefaultCommunicationDetails(), Brand.GMX);
    }

    private Address createDefaultAddress() {
        return new Address("Main St", "1", "12345", "City", "Germany");
    }

    private CommunicationDetails createDefaultCommunicationDetails() {
        return new CommunicationDetails("test@test.com", "1234567890");
    }

    private static class InMemoryCustomerRepository implements CustomerRepository {
        private final Map<UUID, Customer> storage = new HashMap<>();

        @Override
        public void save(final Customer customer) {
            storage.put(customer.getId(), customer);
        }

        @Override
        public Optional<Customer> findById(final UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public Collection<Customer> findAll() {
            return storage.values();
        }

        @Override
        public void delete(final UUID id) throws CustomerNotFoundException {
            if (storage.remove(id) == null) {
                throw new CustomerNotFoundException("Not found");
            }
        }

        @Override
        public void update(final Customer customer) throws CustomerNotFoundException {
            if (!storage.containsKey(customer.getId())) {
                throw new CustomerNotFoundException("Not found");
            }
            storage.put(customer.getId(), customer);
        }
    }
}
