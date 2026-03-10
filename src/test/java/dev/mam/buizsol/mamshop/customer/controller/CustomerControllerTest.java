package dev.mam.buizsol.mamshop.customer.controller;

import dev.mam.buizsol.mamshop.contract.mapper.ContractMapper;
import dev.mam.buizsol.mamshop.customer.dto.AddressRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.AddressResponseDTO;
import dev.mam.buizsol.mamshop.customer.dto.CommunicationDetailsRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.CommunicationDetailsResponseDTO;
import dev.mam.buizsol.mamshop.customer.dto.CustomerRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.CustomerResponseDTO;
import dev.mam.buizsol.mamshop.customer.mapper.CustomerMapper;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.contract.dto.ContractResponseDTO;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.dto.PurchaseRequestDTO;
import dev.mam.buizsol.mamshop.shop.service.ShopService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("CustomerController Tests")
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper()
                        .findAndRegisterModules();

        @MockitoBean
        private ShopService shopService;

        @MockitoBean
        private CustomerMapper customerMapper;

        @MockitoBean
        private ContractMapper contractMapper;

        @Test
        @DisplayName("Positive: Should register customer successfully with valid data")
        void shouldRegisterCustomerSuccessfully() throws Exception {
                AddressRequestDTO addressDto = CustomerTestFactory.createAddressRequestDTO(
                                "Main St", "10", "12345", "Berlin", "Germany");
                CommunicationDetailsRequestDTO commDto = CustomerTestFactory.createCommunicationDetailsRequestDTO(
                                "john.doe@example.com", "+49123456789");
                CustomerRequestDTO requestDto = CustomerTestFactory.createCustomerRequestDTO(
                                "John", "Doe", LocalDate.of(1990, 1, 1), addressDto, null, commDto, Brand.GMX);

                Address address = CustomerTestFactory.createAddress(
                                "Main St", "10", "12345", "Berlin", "Germany");
                CommunicationDetails comm = CustomerTestFactory.createCommunicationDetails(
                                "john.doe@example.com", "+49123456789");
                UUID customerId = UUID.randomUUID();
                Customer customer = CustomerTestFactory.createCustomer(
                                customerId, "John", "Doe", LocalDate.of(1990, 1, 1), address, address, comm, Brand.GMX,
                                CustomerStatus.INACTIVE);

                AddressResponseDTO addressResponseDto = CustomerTestFactory.createAddressResponseDTO(
                                "Main St", "10", "12345", "Berlin", "Germany");
                CommunicationDetailsResponseDTO commResponseDto = CustomerTestFactory
                                .createCommunicationDetailsResponseDTO(
                                                "john.doe@example.com", "+49123456789");

                CustomerResponseDTO responseDto = CustomerTestFactory.createCustomerResponseDTO(
                                customerId, "John", "Doe", LocalDate.of(1990, 1, 1), addressResponseDto,
                                addressResponseDto, commResponseDto,
                                Brand.GMX,
                                CustomerStatus.INACTIVE);

                when(customerMapper.toCustomer(any(CustomerRequestDTO.class))).thenReturn(customer);
                when(shopService.registerCustomer(any(Customer.class))).thenReturn(customer);
                when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(responseDto);

                mockMvc.perform(post("/api/v1/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(customerId.toString()))
                                .andExpect(jsonPath("$.firstName").value("John"))
                                .andExpect(jsonPath("$.lastName").value("Doe"))
                                .andExpect(jsonPath("$.brand").value("GMX"));

                verify(customerMapper).toCustomer(any(CustomerRequestDTO.class));
                verify(shopService).registerCustomer(any(Customer.class));
                verify(customerMapper).toResponseDTO(any(Customer.class));
        }

        @Test
        @DisplayName("Negative: Should return 400 when registering customer with invalid data")
        void shouldReturn400WhenRegisteringWithInvalidData() throws Exception {
                AddressRequestDTO addressDto = CustomerTestFactory.createAddressRequestDTO(
                                "", "10", "12345", "Berlin", "Germany");
                CommunicationDetailsRequestDTO commDto = CustomerTestFactory.createCommunicationDetailsRequestDTO(
                                "invalid-email", "+49123456789");
                CustomerRequestDTO requestDto = CustomerTestFactory.createCustomerRequestDTO(
                                "John", "Doe", LocalDate.of(1990, 1, 1), addressDto, null, commDto, Brand.GMX);

                mockMvc.perform(post("/api/v1/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errorCode").value("REQUEST_VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Positive: Given an existing customer ID, when the client requests the customer, then the customer data is returned successfully")
        void shouldReturnCustomerDataSuccessfully() throws Exception {
                UUID customerId = UUID.randomUUID();

                Address address = CustomerTestFactory.createAddress(
                                "Main St", "10", "12345", "Berlin", "Germany");
                CommunicationDetails comm = CustomerTestFactory.createCommunicationDetails(
                                "john.doe@example.com", "+49123456789");
                Customer customer = CustomerTestFactory.createCustomer(
                                customerId, "John", "Doe",
                                LocalDate.of(1990, 1, 1),
                                address,
                                address,
                                comm,
                                Brand.GMX,
                                CustomerStatus.INACTIVE);

                AddressRequestDTO addressDto = CustomerTestFactory.createAddressRequestDTO(
                                "Main St", "10", "12345", "Berlin", "Germany");
                CommunicationDetailsRequestDTO commDto = CustomerTestFactory.createCommunicationDetailsRequestDTO(
                                "john.doe@example.com", "+49123456789");
                AddressResponseDTO addressResponseDto = CustomerTestFactory.createAddressResponseDTO(
                                "Main St", "10", "12345", "Berlin", "Germany");
                CommunicationDetailsResponseDTO commResponseDto = CustomerTestFactory
                                .createCommunicationDetailsResponseDTO(
                                                "john.doe@example.com", "+49123456789");
                CustomerResponseDTO customerResponseDTO = CustomerTestFactory.createCustomerResponseDTO(
                                customerId,
                                "John",
                                "Doe",
                                LocalDate.of(1990, 1, 1),
                                addressResponseDto,
                                null,
                                commResponseDto,
                                Brand.GMX,
                                CustomerStatus.INACTIVE);

                when(shopService.loadCustomer(any(UUID.class))).thenReturn(customer);
                when(customerMapper.toResponseDTO(any(Customer.class))).thenReturn(customerResponseDTO);

                mockMvc.perform(get("/api/v1/customers/{customerId}", customerId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(customerId.toString()))
                                .andExpect(jsonPath("$.firstName").value("John"))
                                .andExpect(jsonPath("$.lastName").value("Doe"))
                                .andExpect(jsonPath("$.birthDate").value("1990-01-01"))
                                .andExpect(jsonPath("$.brand").value("GMX"))
                                .andExpect(jsonPath("$.status").value("INACTIVE"));

                verify(shopService).loadCustomer(any(UUID.class));
                verify(customerMapper).toResponseDTO(any(Customer.class));

        }

        @Test
        @DisplayName("Negative: Given a non-existing customer ID, when the client requests the customer, then a 404 error is returned")
        void shouldReturn404WhenCustomerNotFound() throws Exception {
                UUID unknownCustomerId = UUID.randomUUID();

                when(shopService.loadCustomer(any(UUID.class)))
                                .thenThrow(new CustomerNotFoundException(
                                                "Customer not found with ID: " + unknownCustomerId));

                mockMvc.perform(get("/api/v1/customers/{customerId}", unknownCustomerId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(shopService).loadCustomer(any(UUID.class));
                verifyNoInteractions(customerMapper);

        }

        @Test
        @DisplayName("Positive: Given an existing customer ID, when the client requests to remove the customer, then the customer status is changed to REMOVED successfully")
        void shouldRemoveCustomerSuccessfully() throws Exception {
                UUID customerId = UUID.randomUUID();

                doNothing().when(shopService).removeCustomer(any(UUID.class));

                mockMvc.perform(delete("/api/v1/customers/{customerId}", customerId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(shopService).removeCustomer(customerId);
                verifyNoInteractions(customerMapper);

        }

        @Test
        @DisplayName("Negative: Given a non-existing customer ID, when the client requests to remove the customer, then a 404 error is returned")
        void shouldReturn404WhenRemovingNonExistingCustomer() throws Exception {
                UUID unknownId = UUID.randomUUID();

                doThrow(new CustomerNotFoundException("Customer not found with ID: " + unknownId))
                                .when(shopService).removeCustomer(unknownId);

                mockMvc.perform(delete("/api/v1/customers/{customerId}", unknownId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(shopService).removeCustomer(unknownId);
                verifyNoInteractions(customerMapper);

        }

        @Test
        @DisplayName("Positive: Given an existing customer ID, when the client requests to activate the customer, then the customer status is changed to ACTIVE successfully")
        void shouldActivateCustomerSuccessfully() throws Exception {
                UUID customerId = UUID.randomUUID();

                doNothing().when(shopService).activateCustomer(any(UUID.class));

                mockMvc.perform(put("/api/v1/customers/{customerId}/activate", customerId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(shopService).activateCustomer(customerId);
                verifyNoInteractions(customerMapper);

        }

        @Test
        @DisplayName("Negative: Given a non-existing customer ID, when the client requests to activate the customer, then a 404 error is returned")
        void shouldReturn404WhenActivatingNonExistingCustomer() throws Exception {
                UUID unknownId = UUID.randomUUID();

                doThrow(new CustomerNotFoundException("Customer not found with ID: " + unknownId))
                                .when(shopService).activateCustomer(unknownId);

                mockMvc.perform(put("/api/v1/customers/{customerId}/activate", unknownId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(shopService).activateCustomer(unknownId);
                verifyNoInteractions(customerMapper);

        }

        @Test
        @DisplayName("Positive: Given an existing customer ID, when the client requests to deactivate the customer, then the customer status is changed to INACTIVE successfully")
        void shouldDeactivateCustomerSuccessfully() throws Exception {
                UUID customerId = UUID.randomUUID();

                doNothing().when(shopService).deactivateCustomer(any(UUID.class));

                mockMvc.perform(put("/api/v1/customers/{customerId}/deactivate", customerId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(shopService).deactivateCustomer(customerId);
                verifyNoInteractions(customerMapper);

        }

        @Test
        @DisplayName("Negative: Given a non-existing customer ID, when the client requests to deactivate the customer, then a 404 error is returned")
        void shouldReturn404WhenDeactivatingNonExistingCustomer() throws Exception {
                UUID unknownId = UUID.randomUUID();

                doThrow(new CustomerNotFoundException("Customer not found with ID: " + unknownId))
                                .when(shopService).deactivateCustomer(unknownId);

                mockMvc.perform(put("/api/v1/customers/{customerId}/deactivate", unknownId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(shopService).deactivateCustomer(unknownId);
                verifyNoInteractions(customerMapper);

        }

        @Test
        @DisplayName("Positive: Given an existing customer ID and valid address data, when the client updates the address, then the address is updated successfully")
        void shouldUpdateCustomerAddressSuccessfully() throws Exception {
                UUID customerId = UUID.randomUUID();
                AddressRequestDTO addressRequestDTO = CustomerTestFactory.createAddressRequestDTO(
                                "Main St", "10", "12345", "Berlin", "Germany");
                Address address = CustomerTestFactory.createAddress(
                                "Main St", "10", "12345", "Berlin", "Germany");

                when(customerMapper.toAddress(addressRequestDTO)).thenReturn(address);
                when(shopService.updateAddress(eq(customerId), eq(address))).thenReturn(mock(Customer.class));

                mockMvc.perform(put("/api/v1/customers/{customerId}/address", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressRequestDTO)))
                                .andExpect(status().isNoContent());

                verify(customerMapper).toAddress(addressRequestDTO);
                verify(shopService).updateAddress(customerId, address);
        }

        @Test
        @DisplayName("Negative: Given a non-existing customer ID, when the client updates the address, then a 404 error is returned")
        void shouldReturn404WhenUpdatingAddressForNonExistingCustomer() throws Exception {
                UUID unknownId = UUID.randomUUID();
                AddressRequestDTO addressRequestDTO = CustomerTestFactory.createAddressRequestDTO(
                                "Main St", "10", "12345", "Berlin", "Germany");
                Address address = CustomerTestFactory.createAddress(
                                "Main St", "10", "12345", "Berlin", "Germany");

                when(customerMapper.toAddress(addressRequestDTO)).thenReturn(address);
                when(shopService.updateAddress(eq(unknownId), eq(address)))
                                .thenThrow(new CustomerNotFoundException("Customer not found with ID: " + unknownId));

                mockMvc.perform(put("/api/v1/customers/{customerId}/address", unknownId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressRequestDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(customerMapper).toAddress(addressRequestDTO);
                verify(shopService).updateAddress(unknownId, address);
        }

        @Test
        @DisplayName("Positive: Given an existing customer ID and valid invoice address, when the client requests to update the invoice address, then the address is updated successfully")
        void shouldUpdateCustomerInvoiceAddressSuccessfully() throws Exception {
                UUID customerId = UUID.randomUUID();
                AddressRequestDTO addressRequestDTO = CustomerTestFactory.createAddressRequestDTO(
                                "Invoice St", "100", "54321", "Munich", "Germany");
                Address address = CustomerTestFactory.createAddress(
                                "Invoice St", "100", "54321", "Munich", "Germany");

                when(customerMapper.toAddress(addressRequestDTO)).thenReturn(address);
                when(shopService.updateInvoiceAddress(eq(customerId), eq(address))).thenReturn(mock(Customer.class));

                mockMvc.perform(put("/api/v1/customers/{customerId}/invoice-address", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressRequestDTO)))
                                .andExpect(status().isNoContent());

                verify(customerMapper).toAddress(addressRequestDTO);
                verify(shopService).updateInvoiceAddress(customerId, address);
        }

        @Test
        @DisplayName("Negative: Given a non-existing customer ID, when the client updates the invoice address, then a 404 error is returned")
        void shouldReturn404WhenUpdatingInvoiceAddressForNonExistingCustomer() throws Exception {
                UUID unknownId = UUID.randomUUID();
                AddressRequestDTO addressRequestDTO = CustomerTestFactory.createAddressRequestDTO(
                                "Invoice St", "100", "54321", "Munich", "Germany");
                Address address = CustomerTestFactory.createAddress(
                                "Invoice St", "100", "54321", "Munich", "Germany");

                when(customerMapper.toAddress(addressRequestDTO)).thenReturn(address);
                when(shopService.updateInvoiceAddress(eq(unknownId), eq(address)))
                                .thenThrow(new CustomerNotFoundException("Customer not found with ID: " + unknownId));

                mockMvc.perform(put("/api/v1/customers/{customerId}/invoice-address", unknownId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addressRequestDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(customerMapper).toAddress(addressRequestDTO);
                verify(shopService).updateInvoiceAddress(unknownId, address);
        }

        @Test
        @DisplayName("Positive: Given an existing customer ID and valid communication details, when the client updates communication details, then the details are updated successfully")
        void shouldUpdateCustomerCommunicationDetailsSuccessfully() throws Exception {
                UUID customerId = UUID.randomUUID();
                CommunicationDetailsRequestDTO communicationDetailsRequestDTO = CustomerTestFactory
                                .createCommunicationDetailsRequestDTO("test@example.com", "+123456789");
                CommunicationDetails communicationDetails = CustomerTestFactory
                                .createCommunicationDetails("test@example.com", "+123456789");

                when(customerMapper.toCommunicationDetails(communicationDetailsRequestDTO))
                                .thenReturn(communicationDetails);
                when(shopService.updateCommunicationDetails(eq(customerId), eq(communicationDetails)))
                                .thenReturn(mock(Customer.class));

                mockMvc.perform(put("/api/v1/customers/{customerId}/communication-details", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(communicationDetailsRequestDTO)))
                                .andExpect(status().isNoContent());

                verify(customerMapper).toCommunicationDetails(communicationDetailsRequestDTO);
                verify(shopService).updateCommunicationDetails(customerId, communicationDetails);
        }

        @Test
        @DisplayName("Negative: Given a non-existing customer ID, when the client updates communication details, then a 404 error is returned")
        void shouldReturn404WhenUpdatingCommunicationDetailsForNonExistingCustomer() throws Exception {
                UUID unknownId = UUID.randomUUID();
                CommunicationDetailsRequestDTO communicationDetailsRequestDTO = CustomerTestFactory
                                .createCommunicationDetailsRequestDTO("test@example.com", "+123456789");
                CommunicationDetails communicationDetails = CustomerTestFactory
                                .createCommunicationDetails("test@example.com", "+123456789");

                when(customerMapper.toCommunicationDetails(communicationDetailsRequestDTO))
                                .thenReturn(communicationDetails);
                when(shopService.updateCommunicationDetails(eq(unknownId), eq(communicationDetails)))
                                .thenThrow(new CustomerNotFoundException("Customer not found with ID: " + unknownId));

                mockMvc.perform(put("/api/v1/customers/{customerId}/communication-details", unknownId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(communicationDetailsRequestDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(customerMapper).toCommunicationDetails(communicationDetailsRequestDTO);
                verify(shopService).updateCommunicationDetails(unknownId, communicationDetails);
        }

        @Test
        @DisplayName("Positive: Given an existing customer and product, when the client requests to purchase the product, then a new contract is created successfully")
        void shouldPurchaseProductSuccessfully() throws Exception {
                UUID customerId = UUID.randomUUID();
                UUID productId = UUID.randomUUID();
                PurchaseRequestDTO purchaseRequestDTO = CustomerTestFactory.createPurchaseRequestDTO(productId);
                Contract contract = CustomerTestFactory.createContract(UUID.randomUUID(), customerId, productId,
                                LocalDate.now(), ContractStatus.INACTIVE);
                ContractResponseDTO contractResponseDTO = CustomerTestFactory.createContractResponseDTO(contract.id(),
                                customerId, productId, contract.creationDate(), contract.status());

                when(shopService.purchaseProduct(customerId, productId)).thenReturn(contract);
                when(contractMapper.toContractResponseDTO(contract)).thenReturn(contractResponseDTO);

                mockMvc.perform(post("/api/v1/customers/{customerId}/purchases", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(purchaseRequestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(contract.id().toString()))
                                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                                .andExpect(jsonPath("$.productId").value(productId.toString()));

                verify(shopService).purchaseProduct(customerId, productId);
                verify(contractMapper).toContractResponseDTO(contract);
        }

        @Test
        @DisplayName("Negative: Given a non-existing customer, when the client requests to purchase a product, then a 404 error is returned")
        void shouldReturn404WhenPurchasingProductForNonExistingCustomer() throws Exception {
                UUID unknownCustomerId = UUID.randomUUID();
                UUID productId = UUID.randomUUID();
                PurchaseRequestDTO purchaseRequestDTO = CustomerTestFactory.createPurchaseRequestDTO(productId);

                when(shopService.purchaseProduct(unknownCustomerId, productId))
                                .thenThrow(new CustomerNotFoundException(
                                                "Customer not found with ID: " + unknownCustomerId));

                mockMvc.perform(post("/api/v1/customers/{customerId}/purchases", unknownCustomerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(purchaseRequestDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(shopService).purchaseProduct(unknownCustomerId, productId);
                verifyNoInteractions(contractMapper);
        }

        @Test
        @DisplayName("Negative: Given a valid customer and non-existing product, when the client requests to purchase the product, then a 404 error is returned")
        void shouldReturn404WhenPurchasingNonExistingProduct() throws Exception {
                UUID customerId = UUID.randomUUID();
                UUID unknownProductId = UUID.randomUUID();
                PurchaseRequestDTO purchaseRequestDTO = CustomerTestFactory.createPurchaseRequestDTO(unknownProductId);

                when(shopService.purchaseProduct(customerId, unknownProductId))
                                .thenThrow(new CustomerNotFoundException(
                                                "Product not found with ID: " + unknownProductId));

                mockMvc.perform(post("/api/v1/customers/{customerId}/purchases", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(purchaseRequestDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("Customer not found"));

                verify(shopService).purchaseProduct(customerId, unknownProductId);
                verifyNoInteractions(contractMapper);
        }

        @Test
        @DisplayName("Negative: Given an existing customer and invalid purchase data, when the client requests to purchase the product, then a 400 error is returned")
        void shouldReturn400WhenPurchasingWithInvalidData() throws Exception {
                UUID customerId = UUID.randomUUID();
                PurchaseRequestDTO invalidRequest = CustomerTestFactory.createPurchaseRequestDTO(null);

                mockMvc.perform(post("/api/v1/customers/{customerId}/purchases", customerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(shopService);
                verifyNoInteractions(contractMapper);
        }

}