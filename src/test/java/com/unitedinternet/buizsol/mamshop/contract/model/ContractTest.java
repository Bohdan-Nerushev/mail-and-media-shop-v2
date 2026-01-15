package com.unitedinternet.buizsol.mamshop.contract.model;

import com.unitedinternet.buizsol.mamshop.contract.exception.BrandMismatchException;
import com.unitedinternet.buizsol.mamshop.contract.exception.CustomerNotActiveException;
import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.customer.model.Customer;
import com.unitedinternet.buizsol.mamshop.customer.model.CustomerStatus;
import com.unitedinternet.buizsol.mamshop.product.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContractTest {

    @Test
    @DisplayName("01. Creating a contract for an active customer with the corresponding product")
    void shouldCreateContractWhenCustomerIsActiveAndBrandsMatch() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(customer.getId()).thenReturn(customerId);
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getId()).thenReturn(productId);
        when(product.getBrand()).thenReturn(Brand.GMX);

        Contract contract = new Contract(customer, product);

        assertNotNull(contract.getId());
        assertEquals(customerId, contract.getCustomerId());
        assertEquals(productId, contract.getProductId());
        assertEquals(ContractStatus.ACTIVE, contract.getStatus());
    }

    @Test
    @DisplayName("02. Verify that only IDs are saved, not objects")
    void shouldStoreOnlyIdsAndNotFullObjects() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(customer.getId()).thenReturn(customerId);
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getId()).thenReturn(productId);
        when(product.getBrand()).thenReturn(Brand.GMX);

        Contract contract = new Contract(customer, product);

        assertNotNull(contract.getCustomerId());
        assertEquals(customerId, contract.getCustomerId());
        assertNotNull(contract.getProductId());
        assertEquals(productId, contract.getProductId());
    }

    @Test
    @DisplayName("03. Checking the creation date")
    void shouldSetCreationDateToCurrentDate() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);

        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getId()).thenReturn(UUID.randomUUID());
        when(product.getBrand()).thenReturn(Brand.GMX);

        Contract contract = new Contract(customer, product);

        assertEquals(LocalDate.now(), contract.getCreationDate());
    }

    @Test
    @DisplayName("04. Attempting to create with different brands → BrandMismatchException")
    void shouldThrowExceptionWhenBrandsDoNotMatch() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);

        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getBrand()).thenReturn(Brand.WEB_DE);

        assertThrows(BrandMismatchException.class, () -> new Contract(customer, product));
    }

    @Test
    @DisplayName("05. Attempting to create for an inactive customer → CustomerNotActiveException")
    void shouldThrowExceptionWhenCustomerIsNotActive() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);

        when(customer.getStatus()).thenReturn(CustomerStatus.INACTIVE);
        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getBrand()).thenReturn(Brand.GMX);

        assertThrows(CustomerNotActiveException.class, () -> new Contract(customer, product));
    }

    @Test
    @DisplayName("06. Attempting to create a client with a null parameter → IllegalArgumentException")
    void shouldThrowExceptionWhenCustomerIsNull() {
        Product product = mock(Product.class);
        assertThrows(IllegalArgumentException.class, () -> new Contract(null, product));
    }

    @Test
    @DisplayName("07. Attempting to create a product with a null parameter → IllegalArgumentException")
    void shouldThrowExceptionWhenProductIsNull() {
        Customer customer = mock(Customer.class);
        assertThrows(IllegalArgumentException.class, () -> new Contract(customer, null));
    }

    @ParameterizedTest
    @EnumSource(Brand.class)
    @DisplayName("08. Parameterized test: creating a contract for all brands")
    void shouldCreateContractForAllBrands(Brand brand) {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);

        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(customer.getBrand()).thenReturn(brand);
        when(product.getId()).thenReturn(UUID.randomUUID());
        when(product.getBrand()).thenReturn(brand);

        Contract contract = new Contract(customer, product);

        assertNotNull(contract);
    }

    @Test
    @DisplayName("09. Contract status update")
    void shouldUpdateContractStatus() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);

        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getId()).thenReturn(UUID.randomUUID());
        when(product.getBrand()).thenReturn(Brand.GMX);

        Contract contract = new Contract(customer, product);
        assertEquals(ContractStatus.ACTIVE, contract.getStatus());

        contract.deactivate();
        assertEquals(ContractStatus.INACTIVE, contract.getStatus());

        contract.activate();
        assertEquals(ContractStatus.ACTIVE, contract.getStatus());

        contract.updateStatus(ContractStatus.INACTIVE);
        assertEquals(ContractStatus.INACTIVE, contract.getStatus());
    }

    @Test
    @DisplayName("10. Attempting to update status to null → IllegalArgumentException")
    void shouldThrowExceptionWhenUpdatingStatusWithNull() {
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);

        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(customer.getBrand()).thenReturn(Brand.GMX);
        when(product.getId()).thenReturn(UUID.randomUUID());
        when(product.getBrand()).thenReturn(Brand.GMX);

        Contract contract = new Contract(customer, product);

        assertThrows(IllegalArgumentException.class, () -> contract.updateStatus(null));
    }
}
