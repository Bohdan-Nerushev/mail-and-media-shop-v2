package com.unitedinternet.buizsol.mamshop.customer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

class CustomerTest {

    private Address mainAddress;
    private CommunicationDetails communicationDetails;

    @BeforeEach
    void setUp() {
        mainAddress = new Address("Main St", "10", "12345", "Berlin", "Germany");
        communicationDetails = new CommunicationDetails("test@gmx.de", "0123456789");
    }

    @Test
    @DisplayName("1. Positive: Successful creation with all required data and generated ID")
    void shouldCreateCustomerWhenDataIsValid() {
        Customer customer = new Customer(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                mainAddress,
                null,
                communicationDetails,
                Brand.GMX);

        Assertions.assertNotNull(customer.getId());
        Assertions.assertTrue(customer.getId() > 0);
        Assertions.assertEquals("John", customer.getFirstName());
        Assertions.assertEquals("Doe", customer.getLastName());
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), customer.getBirthDate());
        Assertions.assertEquals(mainAddress, customer.getAddress());
        Assertions.assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        Assertions.assertEquals(Brand.GMX, customer.getBrand());
    }

    @Test
    @DisplayName("2. Positive: Invoice address fallback to main address")
    void shouldUseMainAddressAsInvoiceAddressWhenNotProvided() {
        Customer customer = new Customer(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                mainAddress,
                null,
                communicationDetails,
                Brand.GMX);

        Assertions.assertEquals(customer.getAddress(), customer.getInvoiceAddress());
        Assertions.assertSame(customer.getAddress(), customer.getInvoiceAddress());
    }

    @Test
    @DisplayName("3. Positive: Address duplication handling by value")
    void shouldHandleAddressesWithSameContentAsDuplicateButDifferentObjects() {
        Address duplicateAddress = new Address("Main St", "10", "12345", "Berlin", "Germany");

        Customer customer = new Customer(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                mainAddress,
                duplicateAddress,
                communicationDetails,
                Brand.GMX);

        Assertions.assertEquals(customer.getAddress(), customer.getInvoiceAddress());
        Assertions.assertNotSame(customer.getAddress(), customer.getInvoiceAddress());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("4. Negative: Validation of first name field in constructor")
    void shouldThrowExceptionWhenFirstNameIsInvalid(String invalidName) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Customer(invalidName, "Doe", LocalDate.now(),
                mainAddress, null, communicationDetails, Brand.GMX));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("5. Negative: Validation of last name field in constructor")
    void shouldThrowExceptionWhenLastNameIsInvalid(String invalidName) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Customer("John", invalidName, LocalDate.now(),
                mainAddress, null, communicationDetails, Brand.GMX));
    }

    @Test
    @DisplayName("6. Negative: Validation of birth date (null check)")
    void shouldThrowExceptionWhenBirthDateIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Customer("John", "Doe", null, mainAddress, null, communicationDetails, Brand.GMX));
    }

    @Test
    @DisplayName("7. Negative: Validation of address (null check)")
    void shouldThrowExceptionWhenAddressIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Customer("John", "Doe", LocalDate.now(), null, null, communicationDetails, Brand.GMX));
    }

    @Test
    @DisplayName("8. Negative: Null status in setter")
    void shouldThrowExceptionWhenSettingNullStatus() {
        Customer customer = createDefaultCustomer();
        Assertions.assertThrows(IllegalArgumentException.class, () -> customer.setStatus(null));
    }

    @Test
    @DisplayName("9. Boundary: Extremely long name handling")
    void shouldHandleExtremelyLongNames() {
        String longName = "A".repeat(2000);
        Customer customer = new Customer(
                longName,
                longName,
                LocalDate.of(1990, 1, 1),
                mainAddress,
                null,
                communicationDetails,
                Brand.GMX);
        Assertions.assertEquals(longName, customer.getFirstName());
        Assertions.assertEquals(longName, customer.getLastName());
    }

    @Test
    @DisplayName("10. Boundary: Birth date as today")
    void shouldAllowBirthDateToBeToday() {
        LocalDate today = LocalDate.now();
        Customer customer = new Customer(
                "John",
                "Doe",
                today,
                mainAddress,
                null,
                communicationDetails,
                Brand.GMX);
        Assertions.assertEquals(today, customer.getBirthDate());
    }

    @Test
    @DisplayName("11. Boundary: Birth date in far past")
    void shouldAllowBirthDateInFarPast() {
        LocalDate farPast = LocalDate.of(1900, 1, 1);
        Customer customer = new Customer(
                "John",
                "Doe",
                farPast,
                mainAddress,
                null,
                communicationDetails,
                Brand.GMX);
        Assertions.assertEquals(farPast, customer.getBirthDate());
    }

    @ParameterizedTest
    @EnumSource(CustomerStatus.class)
    @DisplayName("12. Positive: Status update functionality")
    void shouldAllowUpdatingStatusToAllPossibleValues(CustomerStatus status) {
        Customer customer = createDefaultCustomer();
        customer.setStatus(status);
        Assertions.assertEquals(status, customer.getStatus());
    }

    @Test
    @DisplayName("13. Positive: Address update functionality")
    void shouldAllowUpdatingAddressesIndependently() {
        Customer customer = createDefaultCustomer();
        Address newAddress = new Address("New St", "20", "54321", "Munich", "Germany");
        Address newInvoiceAddress = new Address("Invoice St", "5", "11111", "Hamburg", "Germany");

        customer.setAddress(newAddress);
        customer.setInvoiceAddress(newInvoiceAddress);

        Assertions.assertEquals(newAddress, customer.getAddress());
        Assertions.assertEquals(newInvoiceAddress, customer.getInvoiceAddress());
    }

    @Test
    @DisplayName("14. Boundary: ID generation range")
    void shouldGenerateIdWithinPositiveLongRange() {
        Customer customer = createDefaultCustomer();
        Assertions.assertTrue(customer.getId() > 0, "ID should be a positive number");
    }

    @Test
    @DisplayName("15. Positive: ID equality check")
    void shouldCorrectlyIdentifyWhenIdsAreSame() {
        Long sharedId = 12345L;
        Customer customer1 = createWithId(sharedId);
        Customer customer2 = createWithId(sharedId);
        Customer differentCustomer = createWithId(54321L);

        Assertions.assertTrue(customer1.hasSameId(customer2), "IDs should be considered same");
        Assertions.assertFalse(customer1.hasSameId(differentCustomer), "IDs should be considered different");
    }

    @Test
    @DisplayName("16. Negative: Error when IDs are duplicate during uniqueness check")
    void shouldThrowExceptionWhenIdsAreDuplicateDuringUniquenessCheck() {
        Long duplicateId = 999L;
        Customer customer1 = createWithId(duplicateId);
        Customer customer2 = createWithId(duplicateId);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> customer1.verifyIdentificationUniqueness(customer2));
        Assertions.assertTrue(exception.getMessage().contains("Duplicate customer ID"));
    }

    @Test
    @DisplayName("17. Positive: Uniqueness check passes for different IDs")
    void shouldNotThrowExceptionWhenIdsAreDifferentDuringUniquenessCheck() {
        Customer customer1 = createWithId(1L);
        Customer customer2 = createWithId(2L);

        Assertions.assertDoesNotThrow(() -> customer1.verifyIdentificationUniqueness(customer2));
    }

    private Customer createDefaultCustomer() {
        return new Customer(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                mainAddress,
                null,
                communicationDetails,
                Brand.GMX);
    }

    private Customer createWithId(Long id) {
        return new Customer(
                id,
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                mainAddress,
                null,
                communicationDetails,
                Brand.GMX);
    }
}
