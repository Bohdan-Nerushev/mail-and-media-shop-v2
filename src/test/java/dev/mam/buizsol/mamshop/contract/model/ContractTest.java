// package dev.mam.buizsol.mamshop.contract.model;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;
//
// import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
// import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
// import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
// import dev.mam.buizsol.mamshop.customer.model.Brand;
// import dev.mam.buizsol.mamshop.customer.model.Customer;
// import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
// import dev.mam.buizsol.mamshop.product.model.Product;
// import java.time.LocalDate;
// import java.util.UUID;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.EnumSource;
//
// @DisplayName("Contract Tests")
// class ContractTest {
//
//    @Test
//    @DisplayName("Creating a contract for an active customer with the corresponding product")
//    void shouldCreateContractWhenCustomerIsActiveAndBrandsMatch() throws BrandMismatchException {
//        Customer customer = mock(Customer.class);
//        Product product = mock(Product.class);
//        UUID customerId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//
//        when(customer.id()).thenReturn(customerId);
//        when(customer.status()).thenReturn(CustomerStatus.ACTIVE);
//        when(customer.brand()).thenReturn(Brand.GMX);
//        when(product.getId()).thenReturn(productId);
//        when(product.getBrand()).thenReturn(Brand.GMX);
//
//        Contract contract = Contract.create(customer, product);
//
//        assertNotNull(contract.id());
//        assertEquals(customerId, contract.customerId());
//        assertEquals(productId, contract.productId());
//        assertEquals(ContractStatus.INACTIVE, contract.status());
//    }
//
//    @Test
//    @DisplayName("Verify that only IDs are saved, not objects")
//    void shouldStoreOnlyIdsAndNotFullObjects() throws BrandMismatchException {
//        Customer customer = mock(Customer.class);
//        Product product = mock(Product.class);
//        UUID customerId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//
//        when(customer.id()).thenReturn(customerId);
//        when(customer.status()).thenReturn(CustomerStatus.ACTIVE);
//        when(customer.brand()).thenReturn(Brand.GMX);
//        when(product.getId()).thenReturn(productId);
//        when(product.getBrand()).thenReturn(Brand.GMX);
//
//        Contract contract = Contract.create(customer, product);
//
//        assertNotNull(contract.customerId());
//        assertEquals(customerId, contract.customerId());
//        assertNotNull(contract.productId());
//        assertEquals(productId, contract.productId());
//    }
//
//    @Test
//    @DisplayName("Checking the creation date")
//    void shouldSetCreationDateToCurrentDate() throws BrandMismatchException {
//        Customer customer = mock(Customer.class);
//        Product product = mock(Product.class);
//
//        when(customer.id()).thenReturn(UUID.randomUUID());
//        when(customer.status()).thenReturn(CustomerStatus.ACTIVE);
//        when(customer.brand()).thenReturn(Brand.GMX);
//        when(product.getId()).thenReturn(UUID.randomUUID());
//        when(product.getBrand()).thenReturn(Brand.GMX);
//
//        Contract contract = Contract.create(customer, product);
//
//        assertEquals(LocalDate.now(), contract.creationDate());
//    }
//
//    @Test
//    @DisplayName("Attempting to create with different brands → BrandMismatchException")
//    void shouldThrowExceptionWhenBrandsDoNotMatch() {
//        Customer customer = mock(Customer.class);
//        Product product = mock(Product.class);
//
//        when(customer.brand()).thenReturn(Brand.GMX);
//        when(product.getBrand()).thenReturn(Brand.WEB_DE);
//
//        assertThrows(BrandMismatchException.class, () -> Contract.create(customer, product));
//    }
//
//    @Test
//    @DisplayName("Attempting to create for an inactive customer → CustomerNotActiveException")
//    void shouldThrowExceptionWhenCustomerIsNotActive() {
//        Customer customer = mock(Customer.class);
//        Product product = mock(Product.class);
//
//        when(customer.status()).thenReturn(CustomerStatus.INACTIVE);
//        when(customer.brand()).thenReturn(Brand.GMX);
//        when(product.getBrand()).thenReturn(Brand.GMX);
//
//        assertThrows(CustomerNotActiveException.class, () -> Contract.create(customer, product));
//    }
//
//    @Test
//    @DisplayName("Attempting to create a client with a null parameter → IllegalArgumentException")
//    void shouldThrowExceptionWhenCustomerIsNull() {
//        Product product = mock(Product.class);
//        assertThrows(ContractValidationException.class, () -> Contract.create(null, product));
//    }
//
//    @Test
//    @DisplayName("Attempting to create a product with a null parameter → IllegalArgumentException")
//    void shouldThrowExceptionWhenProductIsNull() {
//        Customer customer = mock(Customer.class);
//        assertThrows(ContractValidationException.class, () -> Contract.create(customer, null));
//    }
//
//    @ParameterizedTest
//    @EnumSource(Brand.class)
//    @DisplayName("Parameterized test: creating a contract for all brands")
//    void shouldCreateContractForAllBrands(Brand brand) throws BrandMismatchException {
//        Customer customer = mock(Customer.class);
//        Product product = mock(Product.class);
//
//        when(customer.id()).thenReturn(UUID.randomUUID());
//        when(customer.status()).thenReturn(CustomerStatus.ACTIVE);
//        when(customer.brand()).thenReturn(brand);
//        when(product.getId()).thenReturn(UUID.randomUUID());
//        when(product.getBrand()).thenReturn(brand);
//
//        Contract contract = Contract.create(customer, product);
//
//        assertNotNull(contract);
//    }
//
//    @Test
//    @DisplayName("Contract status update")
//    void shouldUpdateContractStatus() throws BrandMismatchException {
//        Customer customer = mock(Customer.class);
//        Product product = mock(Product.class);
//
//        when(customer.id()).thenReturn(UUID.randomUUID());
//        when(customer.status()).thenReturn(CustomerStatus.ACTIVE);
//        when(customer.brand()).thenReturn(Brand.GMX);
//        when(product.getId()).thenReturn(UUID.randomUUID());
//        when(product.getBrand()).thenReturn(Brand.GMX);
//
//        Contract contract = Contract.create(customer, product);
//        assertEquals(ContractStatus.INACTIVE, contract.status());
//
//        contract = contract.withStatus(ContractStatus.INACTIVE);
//        assertEquals(ContractStatus.INACTIVE, contract.status());
//
//        contract = contract.withStatus(ContractStatus.ACTIVE);
//        assertEquals(ContractStatus.ACTIVE, contract.status());
//
//        contract = contract.withStatus(ContractStatus.INACTIVE);
//        assertEquals(ContractStatus.INACTIVE, contract.status());
//    }
//
//    @Test
//    @DisplayName("Attempting to update status to null → IllegalArgumentException")
//    void shouldThrowExceptionWhenUpdatingStatusWithNull() throws BrandMismatchException {
//        Customer customer = mock(Customer.class);
//        Product product = mock(Product.class);
//
//        when(customer.id()).thenReturn(UUID.randomUUID());
//        when(customer.status()).thenReturn(CustomerStatus.ACTIVE);
//        when(customer.brand()).thenReturn(Brand.GMX);
//        when(product.getId()).thenReturn(UUID.randomUUID());
//        when(product.getBrand()).thenReturn(Brand.GMX);
//
//        Contract contract = Contract.create(customer, product);
//
//        assertThrows(ContractValidationException.class, () -> contract.withStatus(null));
//    }
//
//    @Test
//    @DisplayName("Negative: Failure when creating Contract with null fields")
//    void shouldThrowExceptionWhenContractConstructorHasNullFields() {
//        assertThrows(
//                ContractValidationException.class,
//                () -> new Contract(null, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(),
// ContractStatus.ACTIVE));
//        assertThrows(
//                ContractValidationException.class,
//                () -> new Contract(UUID.randomUUID(), null, UUID.randomUUID(), LocalDate.now(),
// ContractStatus.ACTIVE));
//        assertThrows(
//                ContractValidationException.class,
//                () -> new Contract(UUID.randomUUID(), UUID.randomUUID(), null, LocalDate.now(),
// ContractStatus.ACTIVE));
//        assertThrows(
//                ContractValidationException.class,
//                () -> new Contract(
//                        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, ContractStatus.ACTIVE));
//        assertThrows(
//                ContractValidationException.class,
//                () -> new Contract(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), null));
//    }
// }
