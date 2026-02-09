package dev.mam.buizsol.mamshop.shop.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.PremiumMailProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
import dev.mam.buizsol.mamshop.product.exception.ProductValidationException;
import dev.mam.buizsol.mamshop.shop.exception.CustomerAndProductBrandMismatchException;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import dev.mam.buizsol.mamshop.product.service.ProductService;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceImplIntegrationTest {

        private ShopServiceImpl shopService;
        private ProductService productService;

        @BeforeEach
        void setUp() {
                shopService = (ShopServiceImpl) ShopService.getInstance();
                productService = ProductService.getInstance();
        }

        private Product createDefaultStandardMailProduct(
                        final String name,
                        final Brand brand,
                        final BigDecimal price) {
                return new StandardMailProduct(
                                name,
                                brand,
                                price);
        }

        private Product createDefaultPremiumMailProduct(
                        final String name,
                        final Brand brand,
                        final BigDecimal price) {
                return new PremiumMailProduct(
                                name,
                                brand,
                                price);
        }

        private Address createDefaultAddress(
                        final String street,
                        final String houseNumber,
                        final String zip,
                        final String city,
                        final String country) {
                return new Address(
                                street,
                                houseNumber,
                                zip,
                                city,
                                country);
        }

        private CommunicationDetails createDefaultCommunicationDetails(
                        final String email,
                        final String phoneNumber) {
                return new CommunicationDetails(
                                email,
                                phoneNumber);
        }

        private Customer createDefaultTestCustomer(
                        final String firstName,
                        final String lastName,
                        final LocalDate birthDate,
                        final Brand brand,
                        final Address address,
                        final Address invoiceAddress,
                        final CommunicationDetails communicationDetails) {
                return new Customer(
                                firstName,
                                lastName,
                                birthDate,
                                address,
                                invoiceAddress,
                                communicationDetails,
                                brand);
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Register Customer: Successfully creates and stores a customer (Parameterized)")
        void shouldRegisterCustomerWhenValidDetailsProvided(Brand brand) {

                Customer customer = createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789"));
                Customer registered = shopService.registerCustomer(customer);

                assertNotNull(registered.getId());
                assertEquals(customer.getFirstName(), registered.getFirstName());
                assertEquals(brand, registered.getBrand());
        }

        @Test
        @DisplayName("Register Customer: Fails when input is null (Negative)")
        void shouldThrowExceptionWhenRegisteringNullCustomer() {
                assertThrows(CustomerValidationException.class, () -> shopService.registerCustomer(null));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Load Customer: Successfully retrieves existing customer by ID (Parameterized)")
        void shouldLoadCustomerWhenIdExists(Brand brand) throws CustomerNotFoundException {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                Customer loaded = shopService.loadCustomer(customer.getId());

                assertEquals(customer.getId(), loaded.getId());
                assertEquals(brand, loaded.getBrand());
        }

        @Test
        @DisplayName("Load Customer: Throws exception for non-existent ID (Negative)")
        void shouldThrowExceptionWhenLoadingNonExistentCustomer() {
                assertThrows(CustomerNotFoundException.class, () -> shopService.loadCustomer(UUID.randomUUID()));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Activate Customer: Changes status to ACTIVE (Parameterized)")
        void shouldActivateCustomerWhenIdExists(Brand brand) throws CustomerNotFoundException {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Customer activated = shopService.loadCustomer(customer.getId());
                assertEquals(CustomerStatus.ACTIVE, activated.getStatus());
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Deactivate Customer: Changes status to INACTIVE (Parameterized)")
        void shouldDeactivateCustomerWhenIdExists(Brand brand) throws CustomerNotFoundException {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());
                shopService.deactivateCustomer(customer.getId());

                Customer deactivated = shopService.loadCustomer(customer.getId());
                assertEquals(CustomerStatus.INACTIVE, deactivated.getStatus());
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Deactivate Customer: Succeeds for INACTIVE customer (Idempotency)")
        void shouldBeIdempotentWhenDeactivatingCustomer(Brand brand) throws CustomerNotFoundException {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));

                assertDoesNotThrow(() -> shopService.deactivateCustomer(customer.getId()));
                assertEquals(CustomerStatus.INACTIVE, shopService.loadCustomer(customer.getId()).getStatus());
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Remove Customer: Successfully deletes customer from system (Parameterized)")
        void shouldRemoveCustomerWhenIdExists(Brand brand) throws CustomerNotFoundException {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.removeCustomer(customer.getId());

                assertThrows(CustomerNotFoundException.class, () -> shopService.loadCustomer(customer.getId()));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Update Address: Successfully updates address for ACTIVE customer (Parameterized)")
        void shouldUpdateAddressWhenCustomerIsActive(Brand brand)
                        throws CustomerNotFoundException, CustomerNotActiveException {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Address newAddress = createDefaultAddress("Street_", "2", "12345", "City_", "Country_");
                Customer updated = shopService.updateAddress(customer.getId(), newAddress);

                assertEquals(newAddress.street(), updated.getAddress().street());
                assertEquals(newAddress.city(), updated.getAddress().city());
        }

        @Test
        @DisplayName("Update Address: Fails for INACTIVE customer (Boundary/State)")
        void shouldThrowExceptionWhenUpdatingAddressOfInactiveCustomer() {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                Brand.WEB_DE,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));

                Address newAddress = createDefaultAddress("Street_", "2", "12345", "City_", "Country_");
                assertThrows(CustomerNotActiveException.class,
                                () -> shopService.updateAddress(customer.getId(), newAddress));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Load All Products: Returns products for specified brand (Parameterized)")
        void shouldLoadAllProductsWhenBrandIsSpecified(Brand brand) {
                List<Product> products = shopService.loadAllProductsForBrand(brand);
                assertNotNull(products);
                assertTrue(products.stream().allMatch(p -> p.getBrand() == brand));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Purchase Product: Successfully creates contract for matching brand and active customer (Parameterized)")
        void shouldPurchaseProductWhenCustomerAndProductMatchBrand(Brand brand) throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Product product = createDefaultStandardMailProduct("Standard Mail", brand, new BigDecimal("2.50"));
                productService.createProduct(product);

                Contract contract = shopService.purchaseProduct(customer.getId(), product.getId());
                assertNotNull(contract);
                assertEquals(customer.getId(), contract.getCustomerId());
                assertEquals(product.getId(), contract.getProductId());
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Purchase Product Premium: Successfully creates contract (Parameterized)")
        void shouldPurchasePremiumProductWhenCustomerAndProductMatchBrand(Brand brand) throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Product product = createDefaultPremiumMailProduct("Premium Mail", brand, new BigDecimal("2.50"));
                productService.createProduct(product);

                Contract contract = shopService.purchaseProduct(customer.getId(), product.getId());
                assertNotNull(contract);
                assertEquals(customer.getId(), contract.getCustomerId());
                assertEquals(product.getId(), contract.getProductId());
        }

        @ParameterizedTest
        @CsvSource({
                        "GMX, WEB_DE",
                        "WEB_DE, MAIL_COM",
                        "MAIL_COM, GMX"
        })
        @DisplayName("Purchase Product: Throws exception when brands do not match (Negative)")
        void shouldThrowExceptionWhenPurchaseBrandsDoNotMatch(Brand customerBrand, Brand productBrand)
                        throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                customerBrand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Product product = createDefaultStandardMailProduct("Mismatched Mail", productBrand,
                                new BigDecimal("2.50"));
                productService.createProduct(product);

                assertThrows(CustomerAndProductBrandMismatchException.class,
                                () -> shopService.purchaseProduct(customer.getId(), product.getId()));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Purchase Product: Fails for non-existent product ID (Negative)")
        void shouldThrowExceptionWhenPurchasingNonExistentProduct(Brand brand) throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                assertThrows(ProductNotFoundException.class,
                                () -> shopService.purchaseProduct(customer.getId(), UUID.randomUUID()));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Purchase Standard Mail: Fails when customer is not active (Boundary)")
        void shouldThrowExceptionWhenPurchasingProductForInactiveCustomer(Brand brand) {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                Product product = createDefaultStandardMailProduct("Standard Mail", brand, new BigDecimal("2.50"));

                assertThrows(CustomerNotActiveException.class,
                                () -> shopService.purchaseProduct(customer.getId(), product.getId()));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Purchase Premium Mail: Fails when customer is not active (Boundary)")
        void shouldThrowExceptionWhenPurchasingPremiumProductForInactiveCustomer(Brand brand) {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                Product product = createDefaultPremiumMailProduct("Premium Mail", brand, new BigDecimal("2.50"));

                assertThrows(CustomerNotActiveException.class,
                                () -> shopService.purchaseProduct(customer.getId(), product.getId()));
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Generate Invoice: Returns valid invoice for active customer (Parameterized)")
        void shouldGenerateInvoiceWhenCustomerIsActiveAndHasContracts(Brand brand) throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Product product = createDefaultStandardMailProduct("Mail", brand, new BigDecimal("2.50"));
                productService.createProduct(product);
                Contract contract = shopService.purchaseProduct(customer.getId(), product.getId());
                shopService.activateContract(contract.getId());

                Invoice invoice = shopService.generateInvoice(customer.getId());
                assertNotNull(invoice);
                assertEquals(customer.getId(), invoice.getCustomerId());
                assertFalse(invoice.getItems().isEmpty());
        }

        @ParameterizedTest
        @EnumSource(Brand.class)
        @DisplayName("Load All Contracts: Retrieves list for active customer (Parameterized)")
        void shouldLoadAllContractsWhenCustomerIsActive(Brand brand) throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                brand,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Product product = createDefaultStandardMailProduct("Mail", brand, new BigDecimal("2.50"));
                productService.createProduct(product);
                shopService.purchaseProduct(customer.getId(), product.getId());
                List<Contract> contracts = shopService.loadAllContracts(customer.getId());
                assertFalse(contracts.isEmpty());
                assertEquals(customer.getId(), contracts.get(0).getCustomerId());
        }

        @Test
        @DisplayName("Activate Contract: Updates contract status successfully")
        void shouldActivateContractWhenIdExists() throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                Brand.GMX,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Product product = createDefaultPremiumMailProduct("Premium Mail", Brand.GMX, new BigDecimal("2.50"));
                productService.createProduct(product);
                Contract contract = shopService.purchaseProduct(customer.getId(), product.getId());

                shopService.activateContract(contract.getId());

                List<Contract> contracts = shopService.loadAllContracts(customer.getId());
                Contract updatedContract = contracts.stream()
                                .filter(c -> c.getId().equals(contract.getId()))
                                .findFirst()
                                .orElseThrow();

                assertEquals(ContractStatus.ACTIVE, updatedContract.getStatus());
        }

        @Test
        @DisplayName("Multi-Update: Customer can update multiple fields sequentially if active")
        void shouldAllowSequentialUpdatesWhenCustomerIsActive() throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                Brand.GMX,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                Address newInv = createDefaultAddress("Street_", "1", "12345", "City_", "Country_");
                Customer updated = shopService.updateInvoiceAddress(customer.getId(), newInv);

                assertEquals(newInv.street(), updated.getInvoiceAddress().street());
        }

        @Test
        @DisplayName("Update Communication Details: Successfully updates for ACTIVE customer")
        void shouldUpdateCommunicationDetailsWhenCustomerIsActive() throws Exception {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                Brand.GMX,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                shopService.activateCustomer(customer.getId());

                CommunicationDetails newDetails = createDefaultCommunicationDetails("user_@test-domain.com",
                                "+49-123-456789");
                Customer updated = shopService.updateCommunicationDetails(customer.getId(), newDetails);

                assertEquals(newDetails.email(), updated.getCommunicationDetails().email());
        }

        @Test
        @DisplayName("Negative - Update Address: Fails when ID is null")
        void shouldThrowExceptionWhenUpdatingAddressWithNullId() {
                Address address = createDefaultAddress("Street_", "1", "12345", "City_", "Country_");
                assertThrows(CustomerValidationException.class, () -> shopService.updateAddress(null, address));
        }

        @Test
        @DisplayName("Negative - Load All Contracts: Fails for inactive customer")
        void shouldThrowExceptionWhenLoadingContractsForInactiveCustomer() {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                Brand.GMX,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                assertThrows(CustomerNotActiveException.class, () -> shopService.loadAllContracts(customer.getId()));
        }

        @Test
        @DisplayName("Negative - Update Invoice Address: Fails for inactive customer")
        void shouldThrowExceptionWhenUpdatingInvoiceAddressOfInactiveCustomer() {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                Brand.GMX,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                Address newInv = createDefaultAddress("Street_", "1", "12345", "City_", "Country_");
                assertThrows(CustomerNotActiveException.class,
                                () -> shopService.updateInvoiceAddress(customer.getId(), newInv));
        }

        @Test
        @DisplayName("Negative - Update Communication Details: Fails for inactive customer")
        void shouldThrowExceptionWhenUpdatingCommunicationDetailsOfInactiveCustomer() {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                Brand.GMX,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                CommunicationDetails newComms = createDefaultCommunicationDetails("user_@test-domain.com",
                                "+49-123-456789");
                assertThrows(CustomerNotActiveException.class,
                                () -> shopService.updateCommunicationDetails(customer.getId(), newComms));
        }

        @Test
        @DisplayName("Negative - Generate Invoice: Fails for inactive customer")
        void shouldThrowExceptionWhenGeneratingInvoiceForInactiveCustomer() {
                Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                Brand.GMX,
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
                                createDefaultCommunicationDetails("user_@test-domain.com", "+49-123-456789")));
                assertThrows(CustomerNotActiveException.class, () -> shopService.generateInvoice(customer.getId()));
        }

        @Test
        @DisplayName("Negative - Generate Invoice: Fails for non-existent customer")
        void shouldThrowExceptionWhenGeneratingInvoiceForNonExistentCustomer() {
                assertThrows(CustomerNotFoundException.class, () -> shopService.generateInvoice(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Negative - Remove Customer: Fails for non-existent ID")
        void shouldThrowExceptionWhenRemovingNonExistentCustomer() {
                assertThrows(CustomerNotFoundException.class, () -> shopService.removeCustomer(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Register Product: Successfully registers and returns the product")
        void shouldRegisterProductWhenValidProductProvided() {
                Product product = createDefaultStandardMailProduct("Shop Product", Brand.GMX, new BigDecimal("4.99"));

                Product registered = shopService.registerProduct(product);

                assertNotNull(registered);
                assertEquals(product.getId(), registered.getId());

                java.util.Optional<Product> found = productService.findById(product.getId());
                assertTrue(found.isPresent());
                assertEquals(product.getName(), found.get().getName());
        }

        @Test
        @DisplayName("Register Product: Throws exception when product is null (Negative)")
        void shouldThrowExceptionWhenRegisteringNullProduct() {
                assertThrows(ProductValidationException.class, () -> shopService.registerProduct(null));
        }

        @Test
        @DisplayName("Register Product: Successfully registers different product types")
        void shouldRegisterVariousProductTypesSuccessfully() {
                Product standard = createDefaultStandardMailProduct("Std", Brand.WEB_DE, new BigDecimal("5.00"));
                Product premium = createDefaultPremiumMailProduct("Prem", Brand.WEB_DE, new BigDecimal("10.00"));

                shopService.registerProduct(standard);
                shopService.registerProduct(premium);

                assertTrue(productService.findById(standard.getId()).isPresent());
                assertTrue(productService.findById(premium.getId()).isPresent());
        }
}
