package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
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
import java.util.List;
import java.util.Optional;

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

    public Customer createDefaultCustomer(
            final String firstName,
            final String lastName,
            final LocalDate birthDate,
            final Address address,
            final Address invoiceAddress,
            final CommunicationDetails communicationDetails,
            final Brand brand) {
        return new Customer(
                firstName, lastName, birthDate,
                address, invoiceAddress, communicationDetails, brand);
    }

    @Mock
    private Address address;

    @Mock
    private CommunicationDetails communicationDetails;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    @DisplayName("Create customer with valid data should succeed")
    void shouldCreateCustomerWhenDataIsValid() {
        final Customer customer = customerService.createCustomer(createDefaultCustomer(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                address, null, communicationDetails, Brand.GMX));

        assertNotNull(customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Create customer with repository = null should throw exception")
    void shouldThrowExceptionWhenRepositoryIsNull() {
        assertThrows(CustomerValidationException.class,
                () -> new CustomerServiceImpl(null));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("Create customer with different brands should succeed")
    void shouldCreateCustomerWithDifferentBrands(final Brand brand) {
        final Customer customer = customerService.createCustomer(createDefaultCustomer(
                "John", "Doe", LocalDate.of(1990, 1, 1),
                address, null, communicationDetails, brand));

        assertEquals(brand, customer.getBrand());
        verify(customerRepository).save(any(Customer.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("Create customer with invalid first name should throw exception")
    void shouldThrowExceptionWhenFirstNameIsInvalid(final String firstName) {
        assertThrows(CustomerValidationException.class, () -> customerService.createCustomer(createDefaultCustomer(
                firstName, "Doe", LocalDate.of(1990, 1, 1),
                address, null, communicationDetails, Brand.WEB_DE)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("Create customer with invalid last name should throw exception")
    void shouldThrowExceptionWhenLastNameIsInvalid(final String lastName) {
        assertThrows(CustomerValidationException.class, () -> customerService.createCustomer(createDefaultCustomer(
                "John", lastName, LocalDate.of(1990, 1, 1),
                address, null, communicationDetails, Brand.WEB_DE)));
    }

    @ParameterizedTest
    @CsvSource({
            "1900-01-01, Boundary: very old birth date",
            "2026-01-14, Boundary: today birth date",
            "2024-05-14, Boundary: future birth date",
            "2010-04-14, Boundary: future birth1 date",
            "2010-04-14, Boundary: future birth1 date",
            "1000-01-01, Boundary: future birth2 date",
            "1500-01-01, Boundary: future birth3 date"
    })
    @DisplayName("Create customer with boundary birth dates should succeed")
    void shouldCreateCustomerWithBoundaryBirthDates(final String dateStr) {
        final LocalDate birthDate = LocalDate.parse(dateStr);

        final Customer customer = customerService.createCustomer(createDefaultCustomer(
                "John", "Doe", birthDate,
                address, null, communicationDetails, Brand.GMX));

        assertEquals(birthDate, customer.getBirthDate());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Update address should update customer address but keep unique invoice address")
    void shouldUpdateAddressSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);
        final Address newAddressMock = mock(Address.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.updateAddress(customerId, newAddressMock);

        verify(customerMock).setAddress(newAddressMock);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("Update invoice address should update only invoice address")
    void shouldUpdateInvoiceAddressSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);
        final Address newInvoiceAddressMock = mock(Address.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.updateInvoiceAddress(customerId, newInvoiceAddressMock);

        verify(customerMock).setInvoiceAddress(newInvoiceAddressMock);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("Update communication details should update successfully")
    void shouldUpdateCommunicationDetailsSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);
        final CommunicationDetails newDetailsMock = mock(CommunicationDetails.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.updateCommunicationDetails(customerId, newDetailsMock);

        verify(customerMock).setCommunicationDetails(newDetailsMock);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("Activate customer should change status to ACTIVE")
    void shouldActivateCustomerSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.activateCustomer(customerId);

        verify(customerMock).setStatus(CustomerStatus.ACTIVE);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("Deactivate customer should change status to INACTIVE")
    void shouldDeactivateCustomerSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();
        final Customer customerMock = mock(Customer.class);

        when(customerRepository.getById(customerId)).thenReturn(customerMock);

        customerService.deactivateCustomer(customerId);

        verify(customerMock).setStatus(CustomerStatus.INACTIVE);
        verify(customerRepository).update(customerMock);
    }

    @Test
    @DisplayName("Delete customer should remove customer from repository")
    void shouldDeleteCustomerSuccessfully() throws CustomerNotFoundException {
        final UUID customerId = UUID.randomUUID();

        customerService.deleteCustomer(customerId);

        verify(customerRepository).delete(customerId);
    }

    @Test
    @DisplayName("Operations on non-existent customer should throw CustomerNotFoundException")
    void shouldThrowExceptionWhenPerformingOperationsOnNonExistentCustomer() throws CustomerNotFoundException {
        final UUID randomId = UUID.randomUUID();

        when(customerRepository.getById(randomId)).thenThrow(new CustomerNotFoundException("Not found"));
        doThrow(new CustomerNotFoundException("Not found")).when(customerRepository).delete(randomId);

        assertThrows(CustomerNotFoundException.class, () -> customerService.activateCustomer(randomId));
        assertThrows(CustomerNotFoundException.class, () -> customerService.deactivateCustomer(randomId));
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(randomId));
        assertThrows(CustomerNotFoundException.class,
                () -> customerService.updateAddress(randomId, address));
    }

    @Test
    @DisplayName("Create customer with null first name should throw exception")
    void shouldThrowExceptionWhenCreatingCustomerWithNullFirstName() {
        assertThrows(
                CustomerValidationException.class,
                () -> customerService.createCustomer(createDefaultCustomer(
                        null,
                        "Doe",
                        LocalDate.now(),
                        address,
                        null,
                        communicationDetails,
                        Brand.GMX)));
    }

    @Test
    @DisplayName("Create customer with null last name should throw exception")
    void shouldThrowExceptionWhenCreatingCustomerWithNullLastName() {
        assertThrows(
                CustomerValidationException.class,
                () -> customerService.createCustomer(createDefaultCustomer(
                        "John",
                        null,
                        LocalDate.now(),
                        address,
                        null,
                        communicationDetails,
                        Brand.GMX)));
    }

    @Test
    @DisplayName("Create customer with null birth date should throw exception")
    void shouldThrowExceptionWhenCreatingCustomerWithNullBirthDate() {
        assertThrows(
                CustomerValidationException.class,
                () -> customerService.createCustomer(createDefaultCustomer(
                        "John",
                        "Doe",
                        null,
                        address,
                        null,
                        communicationDetails,
                        Brand.GMX)));
    }

    @Test
    @DisplayName("Create customer with null address should throw exception")
    void shouldThrowExceptionWhenCreatingCustomerWithNullAddress() {
        assertThrows(
                CustomerValidationException.class,
                () -> customerService.createCustomer(createDefaultCustomer(
                        "John",
                        "Doe",
                        LocalDate.now(),
                        null,
                        null,
                        communicationDetails,
                        Brand.GMX)));
    }

    @Test
    @DisplayName("Create customer with null communication details should throw exception")
    void shouldThrowExceptionWhenCreatingCustomerWithNullCommunicationDetails() {
        assertThrows(
                CustomerValidationException.class,
                () -> customerService.createCustomer(createDefaultCustomer(
                        "John",
                        "Doe",
                        LocalDate.now(),
                        address,
                        null,
                        null,
                        Brand.GMX)));
    }

    @Test
    @DisplayName("Create customer with null brand should throw exception")
    void shouldThrowExceptionWhenCreatingCustomerWithNullBrand() {
        assertThrows(
                CustomerValidationException.class,
                () -> customerService.createCustomer(createDefaultCustomer(
                        "John",
                        "Doe",
                        LocalDate.now(),
                        address,
                        null,
                        communicationDetails,
                        null)));
    }

    @Test
    @DisplayName("Activate customer with null ID should throw exception")
    void shouldThrowExceptionWhenActivatingCustomerWithNullId() {
        assertThrows(CustomerValidationException.class, () -> customerService.activateCustomer(null));
    }

    @Test
    @DisplayName("Deactivate customer with null ID should throw exception")
    void shouldThrowExceptionWhenDeactivatingCustomerWithNullId() {
        assertThrows(CustomerValidationException.class, () -> customerService.deactivateCustomer(null));
    }

    @Test
    @DisplayName("Delete customer with null ID should throw exception")
    void shouldThrowExceptionWhenDeletingCustomerWithNullId() {
        assertThrows(CustomerValidationException.class, () -> customerService.deleteCustomer(null));
    }

    @Test
    @DisplayName("Update address with null ID should throw exception")
    void shouldThrowExceptionWhenUpdatingAddressWithNullId() {
        assertThrows(CustomerValidationException.class, () -> customerService.updateAddress(null, address));
    }

    @Test
    @DisplayName("Update invoice address with null ID should throw exception")
    void shouldThrowExceptionWhenUpdatingInvoiceAddressWithNullId() {
        assertThrows(CustomerValidationException.class, () -> customerService.updateInvoiceAddress(null, address));
    }

    @Test
    @DisplayName("Update communication details with null ID should throw exception")
    void shouldThrowExceptionWhenUpdatingCommunicationDetailsWithNullId() {
        assertThrows(CustomerValidationException.class,
                () -> customerService.updateCommunicationDetails(null, communicationDetails));
    }

    @Test
    @DisplayName("Find customer by ID should return customer from repository")
    void shouldReturnCustomerWhenFindingByIdSuccessfully() {
        final UUID id = UUID.randomUUID();
        final Customer customer = mock(Customer.class);
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        final Optional<Customer> result = customerService.findCustomerById(id);

        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findById(id);
    }

    @Test
    @DisplayName("Find all customers should return all customers from repository")
    void shouldReturnAllCustomersWhenFindingAll() {
        final List<Customer> customers = List.of(mock(Customer.class));
        when(customerRepository.findAll()).thenReturn(customers);

        final List<Customer> result = customerService.findAllCustomers();

        assertEquals(customers.size(), result.size());
        assertEquals(customers, result);
        verify(customerRepository).findAll();
    }
}
