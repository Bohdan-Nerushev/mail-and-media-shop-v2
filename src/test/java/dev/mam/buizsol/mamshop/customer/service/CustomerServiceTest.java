package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private Address address;

    @Mock
    private CommunicationDetails communicationDetails;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    @DisplayName("01: Create customer with valid data should succeed")
    void test01_createCustomer_withValidData_shouldSucceed() {
        final Customer customer = customerService.createCustomer(new Customer(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                address, null, communicationDetails, Brand.GMX));

        assertNotNull(customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        verify(customerRepository).save(any(Customer.class));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("02: Create customer with different brands should succeed")
    void test02_createCustomer_withDifferentBrands_shouldSucceed(final Brand brand) {
        final Customer customer = customerService.createCustomer(new Customer(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                address, null, communicationDetails, brand));

        assertEquals(brand, customer.getBrand());
        verify(customerRepository).save(any(Customer.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("03: Create customer with invalid first name should throw exception")
    void test03_createCustomer_withInvalidFirstName_shouldThrowException(final String firstName) {
        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(
                new Customer(firstName, "Doe", LocalDate.of(1990, 1, 1),
                        address, null, communicationDetails, Brand.WEB_DE)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("04: Create customer with invalid last name should throw exception")
    void test04_createCustomer_withInvalidLastName_shouldThrowException(final String lastName) {
        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(
                new Customer("John", lastName, LocalDate.of(1990, 1, 1),
                        address, null, communicationDetails, Brand.WEB_DE)));
    }

    @ParameterizedTest
    @CsvSource({
            "1900-01-01, Boundary: very old birth date",
            "2026-01-13, Boundary: today birth date"
    })
    @DisplayName("05: Create customer with boundary birth dates should succeed")
    void test05_createCustomer_withBoundaryBirthDates_shouldSucceed(final String dateStr) {
        final LocalDate birthDate = LocalDate.parse(dateStr);

        final Customer customer = customerService.createCustomer(new Customer(
                "John", "Doe", birthDate,
                address, null, communicationDetails, Brand.GMX));

        assertEquals(birthDate, customer.getBirthDate());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("06: Update address should update customer address but keep unique invoice address")
    void test06_updateAddress_shouldUpdateAddressSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);
        final Address newAddressMock = mock(Address.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.updateAddress(customerId, newAddressMock);

        verify(customerMock).setAddress(newAddressMock);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("07: Update invoice address should update only invoice address")
    void test07_updateInvoiceAddress_shouldUpdateInvoiceAddressSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);
        final Address newInvoiceAddressMock = mock(Address.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.updateInvoiceAddress(customerId, newInvoiceAddressMock);

        verify(customerMock).setInvoiceAddress(newInvoiceAddressMock);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("08: Update communication details should update successfully")
    void test08_updateCommunicationDetails_shouldUpdateSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);
        final CommunicationDetails newDetailsMock = mock(CommunicationDetails.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.updateCommunicationDetails(customerId, newDetailsMock);

        verify(customerMock).setCommunicationDetails(newDetailsMock);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("09: Activate customer should change status to ACTIVE")
    void test09_activateCustomer_shouldChangeStatusToActive() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.activateCustomer(customerId);

        verify(customerMock).setStatus(CustomerStatus.ACTIVE);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("10: Deactivate customer should change status to INACTIVE")
    void test10_deactivateCustomer_shouldChangeStatusToInactive() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.deactivateCustomer(customerId);

        verify(customerMock).setStatus(CustomerStatus.INACTIVE);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("11: Delete customer should remove customer from repository")
    void test11_deleteCustomer_shouldRemoveFromRepository() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();

        customerService.deleteCustomer(customerId);

        verify(customerRepository).delete(customerId);
    }

    @Test
    @DisplayName("12: Operations on non-existent customer should throw CustomerNotFoundException")
    void test12_opsOnNonExistentCustomer_shouldThrowException() throws CustomerNotFoundException {
        final UUID randomId = UUID.randomUUID();

        when(customerRepository.getById(randomId)).thenThrow(new CustomerNotFoundException("Not found"));
        doThrow(new CustomerNotFoundException("Not found")).when(customerRepository).delete(randomId);

        assertThrows(CustomerNotFoundException.class, () -> customerService.activateCustomer(randomId));
        assertThrows(CustomerNotFoundException.class, () -> customerService.deactivateCustomer(randomId));
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(randomId));
        assertThrows(CustomerNotFoundException.class,
                () -> customerService.updateAddress(randomId, address));
    }

    @ParameterizedTest
    @MethodSource("provideNullMandatoryFields")
    @DisplayName("13: Create customer with any null mandatory field should throw exception")
    void test13_createCustomer_withNullFields_shouldThrowException(
            final String fn, final String ln, final LocalDate bd,
            final Address addr, final CommunicationDetails cd, final Brand b) {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.createCustomer(new Customer(fn, ln, bd, addr, addr, cd, b)));
    }

    private static Stream<Arguments> provideNullMandatoryFields() {
        final Address a = mock(Address.class);
        final CommunicationDetails c = mock(CommunicationDetails.class);
        final LocalDate d = LocalDate.now();
        return Stream.of(
                Arguments.of(null, "Doe", d, a, c, Brand.GMX),
                Arguments.of("John", null, d, a, c, Brand.GMX),
                Arguments.of("John", "Doe", null, a, c, Brand.GMX),
                Arguments.of("John", "Doe", d, null, c, Brand.GMX),
                Arguments.of("John", "Doe", d, a, null, Brand.GMX),
                Arguments.of("John", "Doe", d, a, c, null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "activate", "deactivate", "delete", "updateAddress", "updateInvoiceAddress",
            "updateCommunicationDetails" })
    @DisplayName("14: All ID-based methods should throw exception if ID is null")
    void test14_idBasedMethods_withNullId_shouldThrowException(final String methodName) {
        switch (methodName) {
            case "activate" -> assertThrows(Exception.class, () -> customerService.activateCustomer(null));
            case "deactivate" -> assertThrows(Exception.class, () -> customerService.deactivateCustomer(null));
            case "delete" -> assertThrows(Exception.class, () -> customerService.deleteCustomer(null));
            case "updateAddress" -> assertThrows(Exception.class, () -> customerService.updateAddress(null, address));
            case "updateInvoiceAddress" ->
                assertThrows(Exception.class, () -> customerService.updateInvoiceAddress(null, address));
            case "updateCommunicationDetails" -> assertThrows(Exception.class,
                    () -> customerService.updateCommunicationDetails(null, communicationDetails));
        }
    }

    @Test
    @DisplayName("20: Find customer by ID should return customer from repository")
    void test15_findCustomerById_shouldReturnCustomer() {
        final UUID id = UUID.randomUUID();
        final Customer customer = mock(Customer.class);
        when(customerRepository.findById(id)).thenReturn(java.util.Optional.of(customer));

        final java.util.Optional<Customer> result = customerService.findCustomerById(id);

        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findById(id);
    }

    @Test
    @DisplayName("21: Find all customers should return all customers from repository")
    void test16_findAllCustomers_shouldReturnAllCustomers() {
        final java.util.Collection<Customer> customers = java.util.List.of(mock(Customer.class));
        when(customerRepository.findAll()).thenReturn(customers);

        final java.util.Collection<Customer> result = customerService.findAllCustomers();

        assertEquals(customers.size(), result.size());
        assertEquals(customers, result);
        verify(customerRepository).findAll();
    }

}
