// package dev.mam.buizsol.mamshop.customer.service;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.Mockito.clearInvocations;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;
//
// import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
// import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
// import dev.mam.buizsol.mamshop.customer.model.Brand;
// import dev.mam.buizsol.mamshop.customer.model.Customer;
// import java.util.Collection;
// import java.util.Optional;
// import java.util.UUID;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.EnumSource;
// import org.junit.jupiter.params.provider.ValueSource;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// @DisplayName("CustomerRepository Tests")
// class CustomerRepositoryTest {
//
//    @Mock
//    private Customer customer;
//
//    private CustomerRepository repository;
//
//    @BeforeEach
//    void setUp() {
//        repository = new CustomerRepositoryImpl();
//        if (customer != null) {
//            clearInvocations(customer);
//        }
//    }
//
//    @Test
//    @DisplayName("Should successfully save and retrieve a single customer by its identifier")
//    void shouldSaveAndRetrieveCustomerCorrectly() {
//        final UUID customerId = UUID.randomUUID();
//        when(customer.id()).thenReturn(customerId);
//
//        repository.save(customer);
//
//        final Optional<Customer> found = repository.findById(customerId);
//
//        assertTrue(found.isPresent());
//        assertEquals(customer, found.get());
//    }
//
//    @ParameterizedTest
//    @EnumSource(Brand.class)
//    @DisplayName("Should find all customers even when they belong to different brands")
//    void shouldFindAllCustomersWithDifferentBrands(final Brand brand) {
//        final UUID customerId = UUID.randomUUID();
//        when(customer.id()).thenReturn(customerId);
//        when(customer.brand()).thenReturn(brand);
//
//        repository.save(customer);
//
//        final Collection<Customer> all = repository.findAll();
//
//        assertTrue(all.stream().anyMatch(c -> c.brand() == brand));
//    }
//
//    @Test
//    @DisplayName("Should return empty optional when attempting to find a customer by a non-existent" + " identifier")
//    void shouldReturnEmptyOptionalWhenFindingNonExistentCustomer() {
//        final Optional<Customer> found = repository.findById(UUID.randomUUID());
//        assertFalse(found.isPresent());
//    }
//
//    @Test
//    @DisplayName("Should successfully update existing customer data in storage")
//    void shouldUpdateExistingCustomerCorrectly() throws CustomerNotFoundException {
//        final UUID customerId = UUID.randomUUID();
//        when(customer.id()).thenReturn(customerId);
//
//        repository.save(customer);
//        repository.update(customer);
//
//        final Optional<Customer> found = repository.findById(customerId);
//        assertTrue(found.isPresent());
//        assertEquals(customer, found.get());
//    }
//
//    @Test
//    @DisplayName("Should successfully delete an existing customer from storage")
//    void shouldDeleteExistingCustomerCorrectly() throws CustomerNotFoundException {
//        final UUID customerId = UUID.randomUUID();
//        when(customer.id()).thenReturn(customerId);
//
//        repository.save(customer);
//        repository.delete(customerId);
//
//        final Optional<Customer> found = repository.findById(customerId);
//        assertFalse(found.isPresent());
//    }
//
//    @Test
//    @DisplayName("Should throw CustomerNotFoundException when attempting to delete a non-existent" + " customer")
//    void shouldThrowExceptionWhenDeletingNonExistentCustomer() {
//        final UUID nonExistentId = UUID.randomUUID();
//        assertThrows(CustomerNotFoundException.class, () -> repository.delete(nonExistentId));
//    }
//
//    @Test
//    @DisplayName("Should throw CustomerNotFoundException when attempting to update a non-existent" + " customer")
//    void shouldThrowExceptionWhenUpdatingNonExistentCustomer() {
//        when(customer.id()).thenReturn(UUID.randomUUID());
//        assertThrows(CustomerNotFoundException.class, () -> repository.update(customer));
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException when passing null to save method (Negative)")
//    void shouldThrowExceptionWhenSavingNullCustomer() {
//        assertThrows(CustomerValidationException.class, () -> repository.save(null));
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException when searching by null identifier (Negative)")
//    void shouldThrowExceptionWhenFindingByNullId() {
//        assertThrows(CustomerValidationException.class, () -> repository.findById(null));
//    }
//
//    @Test
//    @DisplayName("Should handle empty storage state returning an empty collection (Boundary)")
//    void shouldReturnEmptyCollectionWhenStorageIsEmpty() {
//        final Collection<Customer> all = repository.findAll();
//        assertTrue(all.isEmpty());
//    }
//
//    @ParameterizedTest
//    @ValueSource(ints = {1, 5, 20})
//    @DisplayName("Should handle multiple concurrent-like entries and maintain correct size (Boundary)")
//    void shouldMaintainCorrectSizeWhenMultipleEntriesAreSaved(final int count) {
//        for (int i = 0; i < count; i++) {
//            final Customer c = mock(Customer.class);
//            when(c.id()).thenReturn(UUID.randomUUID());
//            repository.save(c);
//        }
//        assertEquals(count, repository.findAll().size());
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException when passing null to update method (Negative)")
//    void shouldThrowExceptionWhenUpdatingNullCustomer() {
//        assertThrows(CustomerValidationException.class, () -> repository.update(null));
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException when passing null to delete method (Negative)")
//    void shouldThrowExceptionWhenDeletingNullId() {
//        assertThrows(CustomerValidationException.class, () -> repository.delete(null));
//    }
//
//    @Test
//    @DisplayName("Negativ customer not found Exception")
//    void shouldThrowExceptionWhenGettingByIdNonExistentCustomer() {
//        assertThrows(CustomerNotFoundException.class, () -> repository.getById(UUID.randomUUID()));
//    }
// }
//
