package dev.mam.buizsol.mamshop.shop.service.controller;

import dev.mam.buizsol.mamshop.billing.dto.InvoiceResponseDTO;
import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.billing.model.InvoiceItem;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BillingTestFactory {

    private BillingTestFactory() {}

    public static Invoice createInvoice(
            Brand brand,
            UUID customerId,
            Address address,
            Address invoiceAddress,
            List<InvoiceItem> items,
            BigDecimal discount) {
        dev.mam.buizsol.mamshop.customer.model.Customer customer =
                org.mockito.Mockito.mock(dev.mam.buizsol.mamshop.customer.model.Customer.class);
        org.mockito.Mockito.when(customer.getId()).thenReturn(customerId);
        return new Invoice(brand, customer, address, invoiceAddress, items, discount);
    }

    public static InvoiceResponseDTO createInvoiceResponseDTO(
            Brand brand,
            LocalDate invoiceDate,
            UUID customerId,
            Address address,
            Address invoiceAddress,
            List<InvoiceItem> items,
            BigDecimal totalSetupFee,
            BigDecimal totalMonthlyFee,
            BigDecimal discount,
            BigDecimal totalAmount) {
        return new InvoiceResponseDTO(
                brand,
                invoiceDate,
                customerId,
                new dev.mam.buizsol.mamshop.customer.dto.AddressResponseDTO(
                        address.getStreet(),
                        address.getNumber(),
                        address.getPostcode(),
                        address.getCity(),
                        address.getCountry()),
                new dev.mam.buizsol.mamshop.customer.dto.AddressResponseDTO(
                        invoiceAddress.getStreet(),
                        invoiceAddress.getNumber(),
                        invoiceAddress.getPostcode(),
                        invoiceAddress.getCity(),
                        invoiceAddress.getCountry()),
                items.stream()
                        .map(i -> new dev.mam.buizsol.mamshop.billing.dto.InvoiceItemResponseDTO(
                                i.getProductId(),
                                i.getProductName(),
                                i.getContract().getId(),
                                i.getContractCreationDate(),
                                i.getSetupFee(),
                                i.getMonthlyFee()))
                        .toList(),
                totalSetupFee,
                totalMonthlyFee,
                discount,
                totalAmount);
    }

    public static Address createAddress(String street, String number, String postcode, String city, String country) {
        return new Address(street, number, postcode, city, country);
    }

    public static InvoiceItem createInvoiceItem(
            UUID productId,
            String productName,
            UUID contractId,
            LocalDate contractCreationDate,
            BigDecimal setupFee,
            BigDecimal monthlyFee) {
        dev.mam.buizsol.mamshop.contract.model.Contract contract =
                org.mockito.Mockito.mock(dev.mam.buizsol.mamshop.contract.model.Contract.class);
        org.mockito.Mockito.when(contract.getId()).thenReturn(contractId);
        org.mockito.Mockito.when(contract.getCreationDate()).thenReturn(contractCreationDate);
        return new InvoiceItem(productId, productName, contract, contractCreationDate, setupFee, monthlyFee);
    }
}
