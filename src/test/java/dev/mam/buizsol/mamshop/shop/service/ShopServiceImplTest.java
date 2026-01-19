package dev.mam.buizsol.mamshop.shop.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.PremiumMailProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
import dev.mam.buizsol.mamshop.product.service.ProductService;
import dev.mam.buizsol.mamshop.shop.exception.CustomerAndProductBrandMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceImplTest {

    private ShopService shopService;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        shopService = ShopService.getInstance();
        productService = ProductService.getInstance();
    }

    private Product createDefaultStandardMailProduct(
            final String name,
            final Brand brand,
            final BigDecimal price) {
        Product product = new StandardMailProduct(
                name,
                brand,
                price);
        productService.createProduct(product);
        return product;
    }

    private Product createDefaultPremiumMailProduct(
            final String name,
            final Brand brand,
            final BigDecimal price) {
        Product product = new PremiumMailProduct(
                name,
                brand,
                price);
        productService.createProduct(product);
        return product;
    }

    private Address createDefaultAddress() {
        return new Address(
                "Street ",
                "1",
                "12345",
                "City",
                "Country");
    }

    private CommunicationDetails createDefaultCommunicationDetails() {
        return new CommunicationDetails(
                "user_" + "@test-domain.com",
                "+49-123-456789");
    }

    private Customer createDefaultTestCustomer(Brand brand) {
        return new Customer(
                "FirstName_",
                "LastName_",
                LocalDate.of(1980, 1, 1),
                createDefaultAddress(),
                createDefaultAddress(),
                createDefaultCommunicationDetails(),
                brand);
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("1. Register Customer: Successfully creates and stores a customer (Parameterized)")
    void test01_registerCustomer_Parameterized(Brand brand) {
        Customer customer = createDefaultTestCustomer(brand);
        Customer registered = shopService.registerCustomer(customer);

        assertNotNull(registered.getId());
        assertEquals(customer.getFirstName(), registered.getFirstName());
        assertEquals(brand, registered.getBrand());
    }

    @Test
    @DisplayName("2. Register Customer: Fails when input is null (Negative)")
    void test02_registerCustomer_Negative_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> shopService.registerCustomer(null));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("3. Load Customer: Successfully retrieves existing customer by ID (Parameterized)")
    void test03_loadCustomer_Parameterized(Brand brand) throws CustomerNotFoundException {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        Customer loaded = shopService.loadCustomer(customer.getId());

        assertEquals(customer.getId(), loaded.getId());
        assertEquals(brand, loaded.getBrand());
    }

    @Test
    @DisplayName("4. Load Customer: Throws exception for non-existent ID (Negative)")
    void test04_loadCustomer_Negative_NotFound() {
        assertThrows(CustomerNotFoundException.class, () -> shopService.loadCustomer(UUID.randomUUID()));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("5. Activate Customer: Changes status to ACTIVE (Parameterized)")
    void test05_activateCustomer_Parameterized(Brand brand) throws CustomerNotFoundException {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.activateCustomer(customer.getId());

        Customer activated = shopService.loadCustomer(customer.getId());
        assertEquals(dev.mam.buizsol.mamshop.customer.model.CustomerStatus.ACTIVE, activated.getStatus());
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("6. Remove Customer: Successfully deletes customer from system (Parameterized)")
    void test06_removeCustomer_Parameterized(Brand brand) throws CustomerNotFoundException {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.removeCustomer(customer.getId());

        assertThrows(CustomerNotFoundException.class, () -> shopService.loadCustomer(customer.getId()));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("7. Update Address: Successfully updates address for ACTIVE customer (Parameterized)")
    void test07_updateAddress_Parameterized(Brand brand) throws CustomerNotFoundException, CustomerNotActiveException {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.activateCustomer(customer.getId());

        Address newAddress = createDefaultAddress();
        Customer updated = shopService.updateAddress(customer.getId(), newAddress);

        assertEquals(newAddress.street(), updated.getAddress().street());
        assertEquals(newAddress.city(), updated.getAddress().city());
    }

    @Test
    @DisplayName("8. Update Address: Fails for INACTIVE customer (Boundary/State)")
    void test08_updateAddress_Negative_InactiveCustomer() {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(Brand.WEB_DE));

        Address newAddress = createDefaultAddress();
        assertThrows(CustomerNotActiveException.class, () -> shopService.updateAddress(customer.getId(), newAddress));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("9. Load All Products: Returns products for specified brand (Parameterized)")
    void test09_loadAllProductsForBrand_Parameterized(Brand brand) {
        List<Product> products = shopService.loadAllProductsForBrand(brand);
        assertNotNull(products);
        assertTrue(products.stream().allMatch(p -> p.getBrand() == brand));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("10. Purchase Product: Successfully creates contract for matching brand and active customer (Parameterized)")
    void test10_purchaseProduct_Parameterized(Brand brand) throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.activateCustomer(customer.getId());

        Product product = createDefaultStandardMailProduct("Standard Mail", brand, new BigDecimal("2.50"));

        Contract contract = shopService.purchaseProduct(customer.getId(), product.getId());
        assertNotNull(contract);
        assertEquals(customer.getId(), contract.getCustomerId());
        assertEquals(product.getId(), contract.getProductId());
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("11. Purchase Product Premium: Successfully creates contract (Parameterized)")
    void test11_purchaseProduct_PremiumMail_Parameterized(Brand brand) throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.activateCustomer(customer.getId());

        Product product = createDefaultPremiumMailProduct("Premium Mail", brand, new BigDecimal("2.50"));

        Contract contract = shopService.purchaseProduct(customer.getId(), product.getId());
        assertNotNull(contract);
        assertEquals(customer.getId(), contract.getCustomerId());
        assertEquals(product.getId(), contract.getProductId());
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("12. Purchase Product: Throws exception when brands do not match (Negative)")
    void test12_purchaseProduct_Negative_BrandMismatch(Brand brand) throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.activateCustomer(customer.getId());

        Brand otherBrand = Brand.values()[(brand.ordinal() + 1) % Brand.values().length];
        Product product = createDefaultStandardMailProduct("Mismatched Mail", otherBrand, new BigDecimal("2.50"));

        assertThrows(CustomerAndProductBrandMismatchException.class,
                () -> shopService.purchaseProduct(customer.getId(), product.getId()));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("13. Purchase Product: Fails for non-existent product ID (Negative)")
    void test13_purchaseProduct_Negative_ProductNotFound(Brand brand) throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.activateCustomer(customer.getId());

        assertThrows(ProductNotFoundException.class,
                () -> shopService.purchaseProduct(customer.getId(), UUID.randomUUID()));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("14. Purchase Standard Mail: Fails when customer is not active (Boundary)")
    void test14_purchaseProduct_Negative_InactiveCustomer(Brand brand) {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        Product product = createDefaultStandardMailProduct("Standard Mail", brand, new BigDecimal("2.50"));

        assertThrows(CustomerNotActiveException.class,
                () -> shopService.purchaseProduct(customer.getId(), product.getId()));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("15. Purchase Premium Mail: Fails when customer is not active (Boundary)")
    void test15_purchaseProduct_Negative_InactiveCustomer(Brand brand) {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        Product product = createDefaultPremiumMailProduct("Premium Mail", brand, new BigDecimal("2.50"));

        assertThrows(CustomerNotActiveException.class,
                () -> shopService.purchaseProduct(customer.getId(), product.getId()));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("16. Generate Invoice: Returns valid invoice for active customer (Parameterized)")
    void test16_generateInvoice_Success(Brand brand) throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.activateCustomer(customer.getId());

        Product product = createDefaultStandardMailProduct("Mail", brand, new BigDecimal("2.50"));
        shopService.purchaseProduct(customer.getId(), product.getId());

        Invoice invoice = shopService.generateInvoice(customer.getId());
        assertNotNull(invoice);
        assertEquals(customer.getId(), invoice.getCustomerId());
        assertFalse(invoice.getItems().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("17. Load All Contracts: Retrieves list for active customer (Parameterized)")
    void test17_loadAllContracts_Success(Brand brand) throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(brand));
        shopService.activateCustomer(customer.getId());

        Product product = createDefaultStandardMailProduct("Mail", brand, new BigDecimal("2.50"));
        shopService.purchaseProduct(customer.getId(), product.getId());

        List<Contract> contracts = shopService.loadAllContracts(customer.getId());
        assertFalse(contracts.isEmpty());
        assertEquals(customer.getId(), contracts.get(0).getCustomerId());
    }

    @Test
    @DisplayName("18. Activate Contract: Updates contract status successfully")
    void test18_activateContract_Success() throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(Brand.GMX));
        shopService.activateCustomer(customer.getId());

        Product product = createDefaultPremiumMailProduct("Premium Mail", Brand.GMX, new BigDecimal("2.50"));
        Contract contract = shopService.purchaseProduct(customer.getId(), product.getId());

        shopService.activateContract(contract.getId());

        List<Contract> contracts = shopService.loadAllContracts(customer.getId());
        Contract updatedContract = contracts.stream()
                .filter(c -> c.getId().equals(contract.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(dev.mam.buizsol.mamshop.contract.model.ContractStatus.ACTIVE, updatedContract.getStatus());
    }

    @Test
    @DisplayName("19. Multi-Update: Customer can update multiple fields sequentially if active")
    void test19_sequentialUpdates_Success() throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(Brand.GMX));
        shopService.activateCustomer(customer.getId());

        Address newInv = createDefaultAddress();
        CommunicationDetails newComms = createDefaultCommunicationDetails();

        Customer updated = shopService.updateInvoiceAddress(customer.getId(), newInv);
        updated = shopService.updateCommunicationDetails(updated.getId(), newComms);

        assertEquals(newInv.street(), updated.getInvoiceAddress().street());
        assertEquals(newComms.email(), updated.getCommunicationDetails().email());
    }

    @Test
    @DisplayName("20. Update Communication Details: Successfully updates for ACTIVE customer")
    void test20_updateCommunicationDetails_Success() throws Exception {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(Brand.GMX));
        shopService.activateCustomer(customer.getId());

        CommunicationDetails newDetails = createDefaultCommunicationDetails();
        Customer updated = shopService.updateCommunicationDetails(customer.getId(), newDetails);

        assertEquals(newDetails.email(), updated.getCommunicationDetails().email());
    }

    @Test
    @DisplayName("21. Negative - Update Address: Fails when ID is null")
    void test21_updateAddress_Negative_NullId() {
        Address address = createDefaultAddress();
        assertThrows(IllegalArgumentException.class, () -> shopService.updateAddress(null, address));
    }

    @Test
    @DisplayName("22. Negative - Load All Contracts: Fails for inactive customer")
    void test22_loadAllContracts_Negative_Inactive() {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(Brand.GMX));
        assertThrows(CustomerNotActiveException.class, () -> shopService.loadAllContracts(customer.getId()));
    }

    @Test
    @DisplayName("23. Negative - Update Invoice Address: Fails for inactive customer")
    void test23_updateInvoiceAddress_Negative_Inactive() {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(Brand.GMX));
        Address newInv = createDefaultAddress();
        assertThrows(CustomerNotActiveException.class,
                () -> shopService.updateInvoiceAddress(customer.getId(), newInv));
    }

    @Test
    @DisplayName("24. Negative - Update Communication Details: Fails for inactive customer")
    void test24_updateCommunicationDetails_Negative_Inactive() {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(Brand.GMX));
        CommunicationDetails newComms = createDefaultCommunicationDetails();
        assertThrows(CustomerNotActiveException.class,
                () -> shopService.updateCommunicationDetails(customer.getId(), newComms));
    }

    @Test
    @DisplayName("25. Negative - Generate Invoice: Fails for inactive customer")
    void test25_generateInvoice_Negative_Inactive() {
        Customer customer = shopService.registerCustomer(createDefaultTestCustomer(Brand.GMX));
        assertThrows(CustomerNotActiveException.class, () -> shopService.generateInvoice(customer.getId()));
    }

    @Test
    @DisplayName("26. Negative - Remove Customer: Fails for non-existent ID")
    void test26_removeCustomer_Negative_NotFound() {
        assertThrows(CustomerNotFoundException.class, () -> shopService.removeCustomer(UUID.randomUUID()));
    }
}
