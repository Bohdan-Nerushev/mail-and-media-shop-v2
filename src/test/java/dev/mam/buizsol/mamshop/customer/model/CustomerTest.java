//package dev.mam.buizsol.mamshop.customer.model;
//
//import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.NullAndEmptySource;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNotSame;
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@DisplayName("Customer Record Tests")
//class CustomerTest {
//
//        private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
//
//        private Address mainAddress;
//        private CommunicationDetails communicationDetails;
//
//        @BeforeEach
//        void setUp() {
//                mainAddress = createDefaultAddress("Main St", "10", "12345", "Berlin", "Germany");
//                communicationDetails = createDefaultCommunicationDetails("test@gmx.de", "0123456789");
//        }
//
//        private Customer createDefaultCustomer(
//                        String firstName,
//                        String lastName,
//                        LocalDate birthDate,
//                        Address address,
//                        Address invoiAddress,
//                        CommunicationDetails communicationDetails,
//                        Brand brand) {
//                Customer customer = Customer.create(
//                                firstName,
//                                lastName,
//                                birthDate,
//                                address,
//                                invoiAddress,
//                                communicationDetails,
//                                brand);
//                Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
//                if (!violations.isEmpty()) {
//                        throw new CustomerValidationException("Validation failed");
//                }
//                return customer;
//        }
//
//        private Address createDefaultAddress(
//                        String street,
//                        String number,
//                        String postcode,
//                        String city,
//                        String country) {
//                return new Address(
//                                street,
//                                number,
//                                postcode,
//                                city,
//                                country);
//        }
//
//        private CommunicationDetails createDefaultCommunicationDetails(
//                        String email,
//                        String telephone) {
//                return new CommunicationDetails(
//                                email,
//                                telephone);
//        }
//
//        @Test
//        @DisplayName("Positive: Successful creation with all required data and generated ID")
//        void shouldCreateCustomerWhenDataIsValid() {
//                Customer customer = createDefaultCustomer(
//                                "John",
//                                "Doe",
//                                LocalDate.of(1990, 1, 1),
//                                mainAddress,
//                                null,
//                                communicationDetails,
//                                Brand.GMX);
//
//                assertNotNull(customer.getId());
//                assertNotNull(customer.getId().toString());
//                assertEquals("John", customer.getFirstName());
//                assertEquals("Doe", customer.getLastName());
//                assertEquals(LocalDate.of(1990, 1, 1), customer.getBirthDate());
//                assertEquals(mainAddress, customer.getAddress());
//                assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
//                assertEquals(Brand.GMX, customer.getBrand());
//                assertEquals(customer.toString(),
//                                "Customer{id=" + customer.getId() + ", brand=GMX, status=INACTIVE}");
//        }
//
//        @Test
//        @DisplayName("Positive: Invoice address fallback to main address")
//        void shouldUseMainAddressAsInvoiceAddressWhenNotProvided() {
//                Customer customer = createDefaultCustomer(
//                                "John",
//                                "Doe",
//                                LocalDate.of(1990, 1, 1),
//                                mainAddress,
//                                null,
//                                communicationDetails,
//                                Brand.GMX);
//
//                assertEquals(customer.getAddress(), customer.getInvoiceAddress());
//                assertSame(customer.getAddress(), customer.getInvoiceAddress());
//        }
//
//        @Test
//        @DisplayName("Positive: Address duplication handling by value")
//        void shouldHandleAddressesWithSameContentAsDuplicateButDifferentObjects() {
//                Address duplicateAddress = createDefaultAddress("Main St", "10", "12345", "Berlin", "Germany");
//
//                Customer customer = createDefaultCustomer(
//                                "John",
//                                "Doe",
//                                LocalDate.of(1990, 1, 1),
//                                mainAddress,
//                                duplicateAddress,
//                                communicationDetails,
//                                Brand.GMX);
//
//                assertEquals(customer.getAddress(), customer.getInvoiceAddress());
//                assertNotSame(customer.getAddress(), customer.getInvoiceAddress());
//        }
//
//        @ParameterizedTest
//        @NullAndEmptySource
//        @ValueSource(strings = { " ", "\t", "\n" })
//        @DisplayName("Negative: Validation of first name field in constructor")
//        void shouldThrowExceptionWhenFirstNameIsInvalid(String invalidName) {
//                assertThrows(CustomerValidationException.class,
//                                () -> createDefaultCustomer(invalidName, "Doe", LocalDate.now(),
//                                                mainAddress, null, communicationDetails, Brand.GMX));
//        }
//
//        @ParameterizedTest
//        @NullAndEmptySource
//        @ValueSource(strings = { " ", "\t", "\n" })
//        @DisplayName("Negative: Validation of last name field in constructor")
//        void shouldThrowExceptionWhenLastNameIsInvalid(String invalidName) {
//                assertThrows(CustomerValidationException.class,
//                                () -> createDefaultCustomer("John", invalidName, LocalDate.now(),
//                                                mainAddress, null, communicationDetails, Brand.GMX));
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of birth date (null check)")
//        void shouldThrowExceptionWhenBirthDateIsNull() {
//                assertThrows(CustomerValidationException.class,
//                                () -> createDefaultCustomer("John", "Doe", null, mainAddress, null,
//                                                communicationDetails, Brand.GMX));
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of address (null check)")
//        void shouldThrowExceptionWhenAddressIsNull() {
//                assertThrows(CustomerValidationException.class,
//                                () -> createDefaultCustomer("John", "Doe", LocalDate.now(), null, null,
//                                                communicationDetails, Brand.GMX));
//        }
//
//        @Test
//        @DisplayName("Positive: Activate customer changes status to ACTIVE")
//        void shouldActivateCustomerAndChangeStatus() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
//
//                Customer newCustomer = customer.withStatus(CustomerStatus.ACTIVE);
//
//                assertEquals(CustomerStatus.ACTIVE, newCustomer.getStatus());
//        }
//
//        @Test
//        @DisplayName("Positive: Deactivate customer changes status to INACTIVE")
//        void shouldDeactivateCustomerAndChangeStatus() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                Customer newCustomer = customer.withStatus(CustomerStatus.ACTIVE);
//                assertEquals(CustomerStatus.ACTIVE, newCustomer.getStatus());
//
//                Customer newCustomer2 = newCustomer.withStatus(CustomerStatus.INACTIVE);
//
//                assertEquals(CustomerStatus.INACTIVE, newCustomer2.getStatus());
//        }
//
//        @Test
//        @DisplayName("Boundary: Extremely long firstname and lastname handling")
//        void shouldHandleExtremelyLongNames() {
//                String longName = "A".repeat(201);
//                assertThrows(CustomerValidationException.class,
//                                () -> createDefaultCustomer(longName, longName,
//                                                LocalDate.of(1990, 1, 1),
//                                                mainAddress, null, communicationDetails, Brand.GMX));
//        }
//
//        @Test
//        @DisplayName("Boundary: Birth date as today")
//        void shouldAllowBirthDateToBeToday() {
//                LocalDate today = LocalDate.now().minusDays(1);
//                Customer customer = createDefaultCustomer(
//                                "John",
//                                "Doe",
//                                today,
//                                mainAddress,
//                                null,
//                                communicationDetails,
//                                Brand.GMX);
//                assertEquals(today, customer.getBirthDate());
//        }
//
//        @Test
//        @DisplayName("Boundary: Birth date in far past")
//        void shouldAllowBirthDateInFarPast() {
//                LocalDate farPast = LocalDate.of(1900, 1, 1);
//                Customer customer = createDefaultCustomer(
//                                "John",
//                                "Doe",
//                                farPast,
//                                mainAddress,
//                                null,
//                                communicationDetails,
//                                Brand.GMX);
//                assertEquals(farPast, customer.getBirthDate());
//        }
//
//        @Test
//        @DisplayName("Boundary: ID generation uniqueness")
//        void shouldGenerateUniqueIdsForDifferentCustomers() {
//                Customer customer1 = createDefaultCustomer("John1", "Doe1", LocalDate.of(1991, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                Customer customer2 = createDefaultCustomer("John2", "Doe2", LocalDate.of(1992, 1, 2), mainAddress, null,
//                                communicationDetails, Brand.MAIL_COM);
//                Customer customer3 = createDefaultCustomer("John3", "Doe3", LocalDate.of(1993, 1, 3), mainAddress, null,
//                                communicationDetails, Brand.WEB_DE);
//
//                assertNotNull(customer1.getId(), "ID should not be null");
//                assertNotEquals(customer1.getId(), customer2.getId(), "IDs should be unique");
//                assertNotEquals(customer2.getId(), customer3.getId(), "IDs should be unique");
//                assertNotEquals(customer1.getId(), customer3.getId(), "IDs should be unique");
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of status update (null check)")
//        void shouldThrowExceptionWhenStatusIsNull() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                assertThrows(CustomerValidationException.class, () -> customer.withStatus(null));
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of address update (null check)")
//        void shouldThrowExceptionWhenNewAddressIsNull() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                assertThrows(CustomerValidationException.class, () -> customer.withAddress(null));
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of invoice address update (null check)")
//        void shouldThrowExceptionWhenNewInvoiceAddressIsNull() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                assertThrows(CustomerValidationException.class, () -> customer.withInvoiceAddress(null));
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of communication details update (null check)")
//        void shouldThrowExceptionWhenNewCommunicationDetailsIsNull() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                assertThrows(CustomerValidationException.class, () -> customer.withCommunicationDetails(null));
//        }
//
//        @Test
//        @DisplayName("Coverage: Constructor fallback for invoiceAddress")
//        void shouldFallbackToAddressWhenInvoiceAddressIsNullInConstructor() {
//                Customer customer = new Customer(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990, 1, 1),
//                                mainAddress,
//                                null, communicationDetails, Brand.GMX, CustomerStatus.ACTIVE);
//                assertEquals(mainAddress, customer.getInvoiceAddress());
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of address update (null check)")
//        void shouldThrowCustomerValidationExceptionWhenNewAddressIsNull() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress,
//                                mainAddress,
//                                communicationDetails, Brand.GMX);
//                assertThrows(CustomerValidationException.class, () -> customer.withAddress(null));
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of invoice address update (null check)")
//        void shouldThrowCustomerValidationExceptionWhenNewInvoiceAddressIsNull() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                assertThrows(CustomerValidationException.class, () -> customer.withInvoiceAddress(null));
//        }
//
//        @Test
//        @DisplayName("Negative: Validation of communication details update (null check)")
//        void shouldThrowCustomerValidationExceptionWhenNewCommunicationDetailsIsNull() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                assertThrows(CustomerValidationException.class, () -> customer.withCommunicationDetails(null));
//        }
//
//        @Test
//        @DisplayName("Positive: Update of address details")
//        void shouldUpdateAddressInCustomerWhenNewAddress() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                Address newAddress = new Address("77777", "77777", "77777", "77777", "77777");
//                Customer updated = customer.withAddress(newAddress);
//
//                assertEquals(newAddress, updated.getAddress());
//                assertEquals(customer.getId(), updated.getId());
//                assertEquals(customer.getFirstName(), updated.getFirstName());
//        }
//
//        @Test
//        @DisplayName("Positive: Validation of communication details update")
//        void shouldUpdateCommunicationDetailsInCustomerWhenNewCommunicationDetails() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                CommunicationDetails newDetails = new CommunicationDetails("77777@gmail.com", "77777");
//                Customer updated = customer.withCommunicationDetails(newDetails);
//
//                assertEquals(newDetails, updated.getCommunicationDetails());
//                assertEquals(customer.getId(), updated.getId());
//        }
//
//        @Test
//        @DisplayName("Positive: Update of invoice address details")
//        void shouldUpdateInvoiceAddressInCustomerWhenNewInvoiceAddress() {
//                Customer customer = createDefaultCustomer("John", "Doe", LocalDate.of(1990, 1, 1), mainAddress, null,
//                                communicationDetails, Brand.GMX);
//                Address newInvoiceAddress = new Address("77777", "77777", "77777", "77777", "77777");
//                Customer updated = customer.withInvoiceAddress(newInvoiceAddress);
//
//                assertEquals(newInvoiceAddress, updated.getInvoiceAddress());
//                assertEquals(customer.getId(), updated.getId());
//        }
//
//}
