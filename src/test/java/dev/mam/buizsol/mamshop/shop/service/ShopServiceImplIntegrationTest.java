/*
 * package dev.mam.buizsol.mamshop.shop.service;
 * 
 * import dev.mam.buizsol.mamshop.billing.model.Invoice;
 * import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
 * import dev.mam.buizsol.mamshop.contract.model.Contract;
 * import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
 * import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
 * import
 * dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
 * import dev.mam.buizsol.mamshop.customer.model.Address;
 * import dev.mam.buizsol.mamshop.customer.model.Brand;
 * import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
 * import dev.mam.buizsol.mamshop.customer.model.Customer;
 * import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
 * import dev.mam.buizsol.mamshop.product.model.PremiumMailProduct;
 * import dev.mam.buizsol.mamshop.product.model.Product;
 * import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
 * import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
 * import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
 * import org.junit.jupiter.api.DisplayName;
 * import org.junit.jupiter.api.Test;
 * import org.junit.jupiter.params.ParameterizedTest;
 * import org.junit.jupiter.params.provider.CsvSource;
 * import org.junit.jupiter.params.provider.EnumSource;
 * 
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.boot.test.context.SpringBootTest;
 * import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
 * 
 * import java.math.BigDecimal;
 * import java.time.LocalDate;
 * import java.util.List;
 * import java.util.UUID;
 * 
 * import dev.mam.buizsol.mamshop.product.service.ProductService;
 * import dev.mam.buizsol.mamshop.config.AppConfig;
 * 
 * import static org.junit.jupiter.api.Assertions.*;
 * 
 * @SpringBootTest
 * 
 * @DisplayName("ShopServiceImpl Integration Test")
 * class ShopServiceImplIntegrationTest {
 * 
 * @Autowired
 * private ShopService shopService;
 * 
 * @Autowired
 * private ProductService productService;
 * 
 * private Product createDefaultStandardMailProduct(
 * final String name,
 * final Brand brand,
 * final BigDecimal price) {
 * return new StandardMailProduct(
 * name,
 * brand,
 * price);
 * }
 * 
 * private Product createDefaultPremiumMailProduct(
 * final String name,
 * final Brand brand,
 * final BigDecimal price) {
 * return new PremiumMailProduct(
 * name,
 * brand,
 * price);
 * }
 * 
 * private Address createDefaultAddress(
 * final String street,
 * final String houseNumber,
 * final String zip,
 * final String city,
 * final String country) {
 * return new Address(
 * street,
 * houseNumber,
 * zip,
 * city,
 * country);
 * }
 * 
 * private CommunicationDetails createDefaultCommunicationDetails(
 * final String email,
 * final String phoneNumber) {
 * return new CommunicationDetails(
 * email,
 * phoneNumber);
 * }
 * 
 * private Customer createDefaultTestCustomer(
 * final String firstName,
 * final String lastName,
 * final LocalDate birthDate,
 * final Brand brand,
 * final Address address,
 * final Address invoiceAddress,
 * final CommunicationDetails communicationDetails) {
 * return Customer.create(
 * firstName,
 * lastName,
 * birthDate,
 * address,
 * invoiceAddress,
 * communicationDetails,
 * brand);
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Register Customer: Successfully creates and stores a customer (Parameterized)"
 * )
 * void shouldRegisterCustomerWhenValidDetailsProvided(Brand brand) {
 * 
 * Customer customer = createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789"));
 * Customer registered = shopService.registerCustomer(customer);
 * 
 * assertNotNull(registered.id());
 * assertEquals(customer.firstName(), registered.firstName());
 * assertEquals(brand, registered.brand());
 * }
 * 
 * @Test
 * 
 * @DisplayName("Register Customer: Fails when input is null (Negative)")
 * void shouldThrowExceptionWhenRegisteringNullCustomer() {
 * assertThrows(CustomerValidationException.class, () ->
 * shopService.registerCustomer(null));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Load Customer: Successfully retrieves existing customer by ID (Parameterized)"
 * )
 * void shouldLoadCustomerWhenIdExists(Brand brand) throws
 * CustomerNotFoundException {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * Customer loaded = shopService.loadCustomer(customer.id());
 * 
 * assertEquals(customer.id(), loaded.id());
 * assertEquals(brand, loaded.brand());
 * }
 * 
 * @Test
 * 
 * @DisplayName("Load Customer: Throws exception for non-existent ID (Negative)"
 * )
 * void shouldThrowExceptionWhenLoadingNonExistentCustomer() {
 * assertThrows(CustomerNotFoundException.class, () ->
 * shopService.loadCustomer(UUID.randomUUID()));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Activate Customer: Changes status to ACTIVE (Parameterized)")
 * void shouldActivateCustomerWhenIdExists(Brand brand) throws
 * CustomerNotFoundException {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Customer activated = shopService.loadCustomer(customer.id());
 * assertEquals(CustomerStatus.ACTIVE, activated.status());
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Deactivate Customer: Changes status to INACTIVE (Parameterized)"
 * )
 * void shouldDeactivateCustomerWhenIdExists(Brand brand) throws
 * CustomerNotFoundException {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * shopService.deactivateCustomer(customer.id());
 * 
 * Customer deactivated = shopService.loadCustomer(customer.id());
 * assertEquals(CustomerStatus.INACTIVE, deactivated.status());
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Deactivate Customer: Succeeds for INACTIVE customer (Idempotency)"
 * )
 * void shouldBeIdempotentWhenDeactivatingCustomer(Brand brand) throws
 * CustomerNotFoundException {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * 
 * assertDoesNotThrow(() -> shopService.deactivateCustomer(customer.id()));
 * assertEquals(CustomerStatus.INACTIVE,
 * shopService.loadCustomer(customer.id()).status());
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Remove Customer: Successfully deletes customer from system (Parameterized)"
 * )
 * void shouldRemoveCustomerWhenIdExists(Brand brand) throws
 * CustomerNotFoundException {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.removeCustomer(customer.id());
 * 
 * assertThrows(CustomerNotFoundException.class, () ->
 * shopService.loadCustomer(customer.id()));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Update Address: Successfully updates address for ACTIVE customer (Parameterized)"
 * )
 * void shouldUpdateAddressWhenCustomerIsActive(Brand brand)
 * throws CustomerNotFoundException, CustomerNotActiveException {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Address newAddress = createDefaultAddress("Street_", "2", "12345", "City_",
 * "Country_");
 * Customer updated = shopService.updateAddress(customer.id(), newAddress);
 * 
 * assertEquals(newAddress.street(), updated.address().street());
 * assertEquals(newAddress.city(), updated.address().city());
 * }
 * 
 * @Test
 * 
 * @DisplayName("Update Address: Fails for INACTIVE customer (Boundary/State)")
 * void shouldThrowExceptionWhenUpdatingAddressOfInactiveCustomer() {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * Brand.WEB_DE,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * 
 * Address newAddress = createDefaultAddress("Street_", "2", "12345", "City_",
 * "Country_");
 * assertThrows(CustomerNotActiveException.class,
 * () -> shopService.updateAddress(customer.id(), newAddress));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Load All Products: Returns products for specified brand (Parameterized)"
 * )
 * void shouldLoadAllProductsWhenBrandIsSpecified(Brand brand) {
 * List<Product> products = shopService.loadAllProductsForBrand(brand);
 * assertNotNull(products);
 * assertTrue(products.stream().allMatch(p -> p.getBrand() == brand));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Purchase Product: Successfully creates contract for matching brand and active customer (Parameterized)"
 * )
 * void shouldPurchaseProductWhenCustomerAndProductMatchBrand(Brand brand)
 * throws Exception {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Product product = createDefaultStandardMailProduct("Standard Mail", brand,
 * new BigDecimal("2.50"));
 * productService.createProduct(product);
 * 
 * Contract contract = shopService.purchaseProduct(customer.id(),
 * product.getId());
 * assertNotNull(contract);
 * assertEquals(customer.id(), contract.customerId());
 * assertEquals(product.getId(), contract.productId());
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Purchase Product Premium: Successfully creates contract (Parameterized)"
 * )
 * void shouldPurchasePremiumProductWhenCustomerAndProductMatchBrand(Brand
 * brand) throws Exception {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Product product = createDefaultPremiumMailProduct("Premium Mail", brand, new
 * BigDecimal("2.50"));
 * productService.createProduct(product);
 * 
 * Contract contract = shopService.purchaseProduct(customer.id(),
 * product.getId());
 * assertNotNull(contract);
 * assertEquals(customer.id(), contract.customerId());
 * assertEquals(product.getId(), contract.productId());
 * }
 * 
 * @ParameterizedTest
 * 
 * @CsvSource({
 * "GMX, WEB_DE",
 * "WEB_DE, MAIL_COM",
 * "MAIL_COM, GMX"
 * })
 * 
 * @DisplayName("Purchase Product: Throws exception when brands do not match (Negative)"
 * )
 * void shouldThrowExceptionWhenPurchaseBrandsDoNotMatch(Brand customerBrand,
 * Brand productBrand)
 * throws Exception {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * customerBrand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Product product = createDefaultStandardMailProduct("Mismatched Mail",
 * productBrand,
 * new BigDecimal("2.50"));
 * productService.createProduct(product);
 * 
 * assertThrows(BrandMismatchException.class,
 * () -> shopService.purchaseProduct(customer.id(), product.getId()));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Purchase Product: Fails for non-existent product ID (Negative)"
 * )
 * void shouldThrowExceptionWhenPurchasingNonExistentProduct(Brand brand) throws
 * Exception {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * assertThrows(ProductNotFoundException.class,
 * () -> shopService.purchaseProduct(customer.id(), UUID.randomUUID()));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Purchase Standard Mail: Fails when customer is not active (Boundary)"
 * )
 * void shouldThrowExceptionWhenPurchasingProductForInactiveCustomer(Brand
 * brand) {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * Product product = createDefaultStandardMailProduct("Standard Mail", brand,
 * new BigDecimal("2.50"));
 * 
 * assertThrows(CustomerNotActiveException.class,
 * () -> shopService.purchaseProduct(customer.id(), product.getId()));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Purchase Premium Mail: Fails when customer is not active (Boundary)"
 * )
 * void
 * shouldThrowExceptionWhenPurchasingPremiumProductForInactiveCustomer(Brand
 * brand) {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * Product product = createDefaultPremiumMailProduct("Premium Mail", brand, new
 * BigDecimal("2.50"));
 * 
 * assertThrows(CustomerNotActiveException.class,
 * () -> shopService.purchaseProduct(customer.id(), product.getId()));
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Generate Invoice: Returns valid invoice for active customer (Parameterized)"
 * )
 * void shouldGenerateInvoiceWhenCustomerIsActiveAndHasContracts(Brand brand)
 * throws Exception {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Product product = createDefaultStandardMailProduct("Mail", brand, new
 * BigDecimal("2.50"));
 * productService.createProduct(product);
 * Contract contract = shopService.purchaseProduct(customer.id(),
 * product.getId());
 * shopService.activateContract(contract.id());
 * 
 * Invoice invoice = shopService.generateInvoice(customer.id());
 * assertNotNull(invoice);
 * assertEquals(customer.id(), invoice.customerId());
 * assertFalse(invoice.items().isEmpty());
 * }
 * 
 * @ParameterizedTest
 * 
 * @EnumSource(Brand.class)
 * 
 * @DisplayName("Load All Contracts: Retrieves list for active customer (Parameterized)"
 * )
 * void shouldLoadAllContractsWhenCustomerIsActive(Brand brand) throws Exception
 * {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * brand,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Product product = createDefaultStandardMailProduct("Mail", brand, new
 * BigDecimal("2.50"));
 * productService.createProduct(product);
 * shopService.purchaseProduct(customer.id(), product.getId());
 * List<Contract> contracts = shopService.loadAllContracts(customer.id());
 * assertFalse(contracts.isEmpty());
 * assertEquals(customer.id(), contracts.get(0).customerId());
 * }
 * 
 * @Test
 * 
 * @DisplayName("Activate Contract: Updates contract status successfully")
 * void shouldActivateContractWhenIdExists() throws Exception {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * Brand.GMX,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Product product = createDefaultPremiumMailProduct("Premium Mail", Brand.GMX,
 * new BigDecimal("2.50"));
 * productService.createProduct(product);
 * Contract contract = shopService.purchaseProduct(customer.id(),
 * product.getId());
 * 
 * shopService.activateContract(contract.id());
 * 
 * List<Contract> contracts = shopService.loadAllContracts(customer.id());
 * Contract updatedContract = contracts.stream()
 * .filter(c -> c.id().equals(contract.id()))
 * .findFirst()
 * .orElseThrow();
 * 
 * assertEquals(ContractStatus.ACTIVE, updatedContract.status());
 * }
 * 
 * @Test
 * 
 * @DisplayName("Multi-Update: Customer can update multiple fields sequentially if active"
 * )
 * void shouldAllowSequentialUpdatesWhenCustomerIsActive() throws Exception {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * Brand.GMX,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * Address newInv = createDefaultAddress("Street_", "1", "12345", "City_",
 * "Country_");
 * Customer updated = shopService.updateInvoiceAddress(customer.id(), newInv);
 * 
 * assertEquals(newInv.street(), updated.invoiceAddress().street());
 * }
 * 
 * @Test
 * 
 * @DisplayName("Update Communication Details: Successfully updates for ACTIVE customer"
 * )
 * void shouldUpdateCommunicationDetailsWhenCustomerIsActive() throws Exception
 * {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * Brand.GMX,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * shopService.activateCustomer(customer.id());
 * 
 * CommunicationDetails newDetails =
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789");
 * Customer updated = shopService.updateCommunicationDetails(customer.id(),
 * newDetails);
 * 
 * assertEquals(newDetails.email(), updated.communicationDetails().email());
 * }
 * 
 * @Test
 * 
 * @DisplayName("Negative - Update Address: Fails when ID is null")
 * void shouldThrowExceptionWhenUpdatingAddressWithNullId() {
 * Address address = createDefaultAddress("Street_", "1", "12345", "City_",
 * "Country_");
 * assertThrows(CustomerValidationException.class, () ->
 * shopService.updateAddress(null, address));
 * }
 * 
 * @Test
 * 
 * @DisplayName("Negative - Load All Contracts: Fails for inactive customer")
 * void shouldThrowExceptionWhenLoadingContractsForInactiveCustomer() {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * Brand.GMX,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * assertThrows(CustomerNotActiveException.class, () ->
 * shopService.loadAllContracts(customer.id()));
 * }
 * 
 * @Test
 * 
 * @DisplayName("Negative - Update Invoice Address: Fails for inactive customer"
 * )
 * void shouldThrowExceptionWhenUpdatingInvoiceAddressOfInactiveCustomer() {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * Brand.GMX,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * Address newInv = createDefaultAddress("Street_", "1", "12345", "City_",
 * "Country_");
 * assertThrows(CustomerNotActiveException.class,
 * () -> shopService.updateInvoiceAddress(customer.id(), newInv));
 * }
 * 
 * @Test
 * 
 * @DisplayName("Negative - Update Communication Details: Fails for inactive customer"
 * )
 * void shouldThrowExceptionWhenUpdatingCommunicationDetailsOfInactiveCustomer()
 * {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * Brand.GMX,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * CommunicationDetails newComms =
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789");
 * assertThrows(CustomerNotActiveException.class,
 * () -> shopService.updateCommunicationDetails(customer.id(), newComms));
 * }
 * 
 * @Test
 * 
 * @DisplayName("Negative - Generate Invoice: Fails for inactive customer")
 * void shouldThrowExceptionWhenGeneratingInvoiceForInactiveCustomer() {
 * Customer customer = shopService.registerCustomer(createDefaultTestCustomer(
 * "John",
 * "Doe",
 * LocalDate.of(1990, 1, 1),
 * Brand.GMX,
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultAddress("Street_", "1", "12345", "City_", "Country_"),
 * createDefaultCommunicationDetails("user_@test-domain.com",
 * "+49-123-456789")));
 * assertThrows(CustomerNotActiveException.class, () ->
 * shopService.generateInvoice(customer.id()));
 * }
 * 
 * @Test
 * 
 * @DisplayName("Negative - Generate Invoice: Fails for non-existent customer")
 * void shouldThrowExceptionWhenGeneratingInvoiceForNonExistentCustomer() {
 * assertThrows(CustomerNotFoundException.class, () ->
 * shopService.generateInvoice(UUID.randomUUID()));
 * }
 * 
 * @Test
 * 
 * @DisplayName("Negative - Remove Customer: Fails for non-existent ID")
 * void shouldThrowExceptionWhenRemovingNonExistentCustomer() {
 * assertThrows(CustomerNotFoundException.class, () ->
 * shopService.removeCustomer(UUID.randomUUID()));
 * }
 * 
 * @Test
 * 
 * @DisplayName("Positive - Load All Products for Brand: Returns non-empty list"
 * )
 * void shouldHaveProductsInCatalogAfterInitialization() {
 * List<Product> gmxProducts = shopService.loadAllProductsForBrand(Brand.GMX);
 * List<Product> mailComProducts =
 * shopService.loadAllProductsForBrand(Brand.MAIL_COM);
 * List<Product> webDeProducts =
 * shopService.loadAllProductsForBrand(Brand.WEB_DE);
 * 
 * assertFalse(gmxProducts.isEmpty());
 * assertFalse(mailComProducts.isEmpty());
 * assertFalse(webDeProducts.isEmpty());
 * }
 * }
 */
