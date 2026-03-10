package dev.mam.buizsol.mamshop.contract.controller;

import dev.mam.buizsol.mamshop.contract.dto.ContractResponseDTO;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.mapper.ContractMapper;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
import dev.mam.buizsol.mamshop.shop.service.ShopService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ContractController Infrastructure Tests")
@WebMvcTest(ContractController.class)
class ContractControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ShopService shopService;

        @MockitoBean
        private ContractMapper contractMapper;

        @Test
        @DisplayName(value = "Positive: should return list of contracts when customer exists")
        void shouldLoadAllContractsByCustomerId() throws Exception {
                final UUID customerId = UUID.randomUUID();

                final Address address = new Address("Street", "123", "City", "State", "Zip");
                final CommunicationDetails communicationDetails = new CommunicationDetails(
                                "John@gmail.com",
                                "123456789");

                final Customer customer = Customer.create(
                                "John",
                                "Doe",
                                LocalDate.now()
                                                .minusYears(19),
                                address,
                                address,
                                communicationDetails,
                                Brand.GMX)
                                .withStatus(CustomerStatus.ACTIVE);

                final Product product = new StandardMailProduct(
                                "Standard Mail",
                                Brand.GMX,
                                new BigDecimal("4.99"));

                final Contract mockContract = Contract.create(customer, product);

                final ContractResponseDTO responseDto = new ContractResponseDTO(
                                mockContract.id(),
                                mockContract.customerId(),
                                mockContract.productId(),
                                mockContract.creationDate(),
                                mockContract.status());

                when(shopService.loadAllContracts(any(UUID.class)))
                                .thenReturn(List.of(mockContract, mockContract));

                when(contractMapper.toContractResponseDTO(any(Contract.class)))
                                .thenReturn(responseDto);

                mockMvc.perform(get("/api/v1/contracts/{customerId}", customerId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(mockContract.id()
                                                .toString())))
                                .andExpect(jsonPath("$[0].status", is(ContractStatus.INACTIVE.name())));

                verify(shopService).loadAllContracts(customerId);
        }

        @Test
        @DisplayName(value = "Negative: should return 404 when customer not found")
        void shouldReturn404WhenCustomerNotFound() throws Exception {
                final UUID customerId = UUID.randomUUID();

                when(shopService.loadAllContracts(any(UUID.class)))
                                .thenThrow(new CustomerNotFoundException("Customer not found"));

                mockMvc.perform(get("/api/v1/contracts/{customerId}", customerId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message", is("Customer not found")))
                                .andExpect(jsonPath("$.errorCode", is("CUSTOMER_NOT_FOUND")));

                verify(shopService).loadAllContracts(customerId);
                verifyNoInteractions(contractMapper);
        }

        @Test
        @DisplayName(value = "Negative: should return 404 when customerId segment is missing")
        void shouldReturn404WhenCustomerIdSegmentIsMissing() throws Exception {
                mockMvc.perform(get("/api/v1/contracts")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
                verifyNoInteractions(shopService);
                verifyNoInteractions(contractMapper);
        }

        @Test
        @DisplayName(value = "Positive: should activate contract successfully when IDs are valid")
        void shouldActivateContractSuccessfully() throws Exception {
                final UUID customerId = UUID.randomUUID();
                final UUID contractId = UUID.randomUUID();

                doNothing().when(shopService).activateContract(customerId, contractId);

                mockMvc.perform(put("/api/v1/contracts/{contractId}/{customerId}/activate",
                                contractId, customerId))
                                .andExpect(status().isNoContent());

                verify(shopService).activateContract(customerId, contractId);
        }

        @Test
        @DisplayName(value = "Negative: should return 404 when contract not found during activation")
        void shouldReturn404WhenContractNotFoundDuringActivation() throws Exception {
                final UUID customerId = UUID.randomUUID();
                final UUID contractId = UUID.randomUUID();

                doThrow(new ContractNotFoundException("Contract not found"))
                                .when(shopService).activateContract(any(UUID.class), any(UUID.class));

                mockMvc.perform(put("/api/v1/contracts/{contractId}/{customerId}/activate",
                                contractId, customerId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode", is("CONTRACT_NOT_FOUND")))
                                .andExpect(jsonPath("$.message", is("Contract not found")));

                verify(shopService).activateContract(customerId, contractId);
        }

        @Test
        @DisplayName(value = "Negative: should return 404 when customer not found during activation")
        void shouldReturn404WhenCustomerNotFoundDuringActivation() throws Exception {
                final UUID customerId = UUID.randomUUID();
                final UUID contractId = UUID.randomUUID();

                doThrow(new CustomerNotFoundException("Customer not found"))
                                .when(shopService).activateContract(any(UUID.class), any(UUID.class));

                mockMvc.perform(put("/api/v1/contracts/{contractId}/{customerId}/activate",
                                contractId, customerId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.errorCode", is("CUSTOMER_NOT_FOUND")))
                                .andExpect(jsonPath("$.message", is("Customer not found")));

                verify(shopService).activateContract(customerId, contractId);
        }

        @Test
        @DisplayName(value = "Negative: should return 422 when contract belongs to a different customer")
        void shouldReturn422WhenContractBelongsToDifferentCustomer() throws Exception {
                final UUID customerId = UUID.randomUUID();
                final UUID contractId = UUID.randomUUID();

                doThrow(new BrandMismatchException("Brand mismatch"))
                                .when(shopService).activateContract(any(UUID.class), any(UUID.class));

                mockMvc.perform(put("/api/v1/contracts/{contractId}/{customerId}/activate",
                                contractId, customerId))
                                .andExpect(status().is(422))
                                .andExpect(jsonPath("$.errorCode", is("BRAND_MISMATCH")))
                                .andExpect(jsonPath("$.message", is("Brand mismatch")));

                verify(shopService).activateContract(customerId, contractId);
        }
}
