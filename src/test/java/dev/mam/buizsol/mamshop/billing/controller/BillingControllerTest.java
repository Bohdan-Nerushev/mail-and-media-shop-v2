//package dev.mam.buizsol.mamshop.billing.controller;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import dev.mam.buizsol.mamshop.billing.dto.InvoiceResponseDTO;
//import org.junit.jupiter.api.DisplayName;
//import org.springframework.http.MediaType;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import dev.mam.buizsol.mamshop.billing.mapper.InvoiceMapper;
//import dev.mam.buizsol.mamshop.billing.model.Invoice;
//import dev.mam.buizsol.mamshop.billing.model.InvoiceItem;
//import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
//import dev.mam.buizsol.mamshop.customer.model.Address;
//import dev.mam.buizsol.mamshop.customer.model.Brand;
//import dev.mam.buizsol.mamshop.shop.service.ShopService;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@DisplayName("BillingController Tests")
//@WebMvcTest(BillingController.class)
//public class BillingControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private ShopService shopService;
//
//    @MockitoBean
//    private InvoiceMapper invoiceMapper;
//
//    @Test
//    @DisplayName("Positive: Should generate invoice successfully for valid customer")
//    void shouldGenerateInvoiceSuccessfully() throws Exception {
//
//        UUID customerId = UUID.randomUUID();
//        Brand brand = Brand.GMX;
//        Address address = BillingTestFactory.createAddress(
//                "Main St",
//                "10",
//                "12345",
//                "Berlin",
//                "Germany");
//        InvoiceItem item = BillingTestFactory.createInvoiceItem(
//                UUID.randomUUID(),
//                "Premium Mail",
//                UUID.randomUUID(),
//                LocalDate.now(),
//                new BigDecimal("5.00"),
//                new BigDecimal("10.00"));
//
//        Invoice invoice = BillingTestFactory.createInvoice(
//                brand,
//                customerId,
//                address,
//                address,
//                List.of(item),
//                BigDecimal.ZERO);
//
//        InvoiceResponseDTO responseDto = BillingTestFactory.createInvoiceResponseDTO(
//                brand,
//                LocalDate.now(),
//                customerId,
//                address,
//                address,
//                List.of(item),
//                new BigDecimal("5.00"),
//                new BigDecimal("10.00"),
//                BigDecimal.ZERO,
//                new BigDecimal("15.00"));
//
//        when(shopService.generateInvoice(customerId))
//                .thenReturn(invoice);
//        when(invoiceMapper.toInvoiceResponseDTO(invoice))
//                .thenReturn(responseDto);
//
//        mockMvc.perform(post("/api/v1/billing/{customerId}/invoice", customerId)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
//                .andExpect(jsonPath("$.brand").value(brand.name()))
//                .andExpect(jsonPath("$.totalAmount").value(15.00));
//
//        verify(shopService).generateInvoice(customerId);
//        verify(invoiceMapper).toInvoiceResponseDTO(invoice);
//    }
//
//    @Test
//    @DisplayName("Negative: Should return 404 when customer not found")
//    void shouldReturn404WhenCustomerNotFound() throws Exception {
//        UUID customerId = UUID.randomUUID();
//        when(shopService.generateInvoice(customerId))
//                .thenThrow(new CustomerNotFoundException("Customer not found"));
//
//        mockMvc.perform(post("/api/v1/billing/{customerId}/invoice", customerId)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errorCode").value("CUSTOMER_NOT_FOUND"))
//                .andExpect(jsonPath("$.message").value("Customer not found"));
//
//        verify(shopService).generateInvoice(customerId);
//    }
//
//    @Test
//    @DisplayName("Negative: Should return 400 when customer ID format is invalid")
//    void shouldReturn400WhenCustomerIdIsInvalid() throws Exception {
//
//        String invalidCustomerId = "not-a-uuid";
//
//        mockMvc.perform(post("/api/v1/billing/{customerId}/invoice", invalidCustomerId)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errorCode").value("TYPE_MISMATCH"));
//    }
//}