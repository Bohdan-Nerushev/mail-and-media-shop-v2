package dev.mam.buizsol.mamshop.customer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

class CustomerTest {

        private Address mainAddress;
        private CommunicationDetails communicationDetails;

        @BeforeEach
        void setUp() {
                mainAddress = createDefaultAddress("Main St", "10", "12345", "Berlin", "Germany");
                communicationDetails = createDefaultCommunicationDetails("test@gmx.de", "0123456789");
        }

        private Customer createDefaultCustomer(
                        String firstName,
                        String lastName,
                        LocalDate birthDate,
                        Address address,
                        Address invoiAddress,
                        CommunicationDetails communicationDetails,
                        Brand brand) {
                return new Customer(
                                firstName,
                                lastName,
                                birthDate,
                                address,
                                invoiAddress,
                                communicationDetails,
                                brand);
        }

        private Address createDefaultAddress(
                        String street,
                        String number,
                        String postcode,
                        String city,
                        String country) {
                return new Address(
                                street,
                                number,
                                postcode,
                                city,
                                country);
        }

        private CommunicationDetails createDefaultCommunicationDetails(
                String email,
                String telephone) {
                return new CommunicationDetails(
                        email,
                        telephone);
        }

        @Test
        @DisplayName("1. Positive: Successful creation with all required data and generated ID")
        void shouldCreateCustomerWhenDataIsValid() {
                Customer customer = createDefaultCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                mainAddress,
                                null,
                                communicationDetails,
                                Brand.GMX);

                Assertions.assertNotNull(customer.getId());
                Assertions.assertNotNull(customer.getId().toString());
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
                Customer customer = createDefaultCustomer(
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
                Address duplicateAddress = createDefaultAddress("Main St", "10", "12345", "Berlin", "Germany");

                Customer customer = createDefaultCustomer(
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
                Assertions.assertThrows(IllegalArgumentException.class,
                                () -> createDefaultCustomer(invalidName, "Doe", LocalDate.now(),
                                                mainAddress, null, communicationDetails, Brand.GMX));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { " ", "\t", "\n" })
        @DisplayName("5. Negative: Validation of last name field in constructor")
        void shouldThrowExceptionWhenLastNameIsInvalid(String invalidName) {
                Assertions.assertThrows(IllegalArgumentException.class,
                                () -> createDefaultCustomer("John", invalidName, LocalDate.now(),
                                                mainAddress, null, communicationDetails, Brand.GMX));
        }

        @Test
        @DisplayName("6. Negative: Validation of birth date (null check)")
        void shouldThrowExceptionWhenBirthDateIsNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                () -> createDefaultCustomer("John", "Doe", null, mainAddress, null,
                                                communicationDetails, Brand.GMX));
        }

        @Test
        @DisplayName("7. Negative: Validation of address (null check)")
        void shouldThrowExceptionWhenAddressIsNull() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                () -> createDefaultCustomer("John", "Doe", LocalDate.now(), null, null,
                                                communicationDetails, Brand.GMX));
        }

        @Test
        @DisplayName("8. Positive: Activate customer changes status to ACTIVE")
        void shouldActivateCustomerAndChangeStatus() {
                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
                                communicationDetails, Brand.GMX);
                Assertions.assertEquals(CustomerStatus.INACTIVE, customer.getStatus());

                customer.setStatus(CustomerStatus.ACTIVE);

                Assertions.assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
        }

        @Test
        @DisplayName("9. Positive: Deactivate customer changes status to INACTIVE")
        void shouldDeactivateCustomerAndChangeStatus() {
                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
                                communicationDetails, Brand.GMX);
                customer.setStatus(CustomerStatus.ACTIVE);
                Assertions.assertEquals(CustomerStatus.ACTIVE, customer.getStatus());

                customer.setStatus(CustomerStatus.INACTIVE);
                Assertions.assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        }

        @Test
        @DisplayName("10. Boundary: Extremely long name handling")
        void shouldHandleExtremelyLongNames() {
                String longName = "A".repeat(2000);
                Customer customer = createDefaultCustomer(
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
        @DisplayName("11. Boundary: Birth date as today")
        void shouldAllowBirthDateToBeToday() {
                LocalDate today = LocalDate.now();
                Customer customer = createDefaultCustomer(
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
        @DisplayName("12. Boundary: Birth date in far past")
        void shouldAllowBirthDateInFarPast() {
                LocalDate farPast = LocalDate.of(1900, 1, 1);
                Customer customer = createDefaultCustomer(
                                "John",
                                "Doe",
                                farPast,
                                mainAddress,
                                null,
                                communicationDetails,
                                Brand.GMX);
                Assertions.assertEquals(farPast, customer.getBirthDate());
        }

        @Test
        @DisplayName("13. Boundary: ID generation uniqueness")
        void shouldGenerateUniqueIdsForDifferentCustomers() {
                Customer customer1 = createDefaultCustomer("John1", "Doe1", LocalDate.of(1991, 1, 1), mainAddress, null,
                                communicationDetails, Brand.GMX);
                Customer customer2 = createDefaultCustomer("John2", "Doe2", LocalDate.of(1992, 1, 2), mainAddress, null,
                                communicationDetails, Brand.MAIL_COM);
                Customer customer3 = createDefaultCustomer("John3", "Doe3", LocalDate.of(1993, 1, 3), mainAddress, null,
                                communicationDetails, Brand.WEB_DE);

                Assertions.assertNotNull(customer1.getId(), "ID should not be null");
                Assertions.assertNotEquals(customer1.getId(), customer2.getId(), "IDs should be unique");
                Assertions.assertNotEquals(customer2.getId(), customer3.getId(), "IDs should be unique");
                Assertions.assertNotEquals(customer1.getId(), customer3.getId(), "IDs should be unique");
        }
}
