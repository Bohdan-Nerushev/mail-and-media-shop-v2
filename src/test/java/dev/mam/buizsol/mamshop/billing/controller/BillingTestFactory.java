//package dev.mam.buizsol.mamshop.billing.controller;
//
//import dev.mam.buizsol.mamshop.billing.dto.InvoiceResponseDTO;
//import dev.mam.buizsol.mamshop.billing.model.Invoice;
//import dev.mam.buizsol.mamshop.billing.model.InvoiceItem;
//import dev.mam.buizsol.mamshop.customer.model.Address;
//import dev.mam.buizsol.mamshop.customer.model.Brand;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//public class BillingTestFactory {
//
//        private BillingTestFactory() {
//        }
//
//        public static Invoice createInvoice(
//                        Brand brand,
//                        UUID customerId,
//                        Address address,
//                        Address invoiceAddress,
//                        List<InvoiceItem> items,
//                        BigDecimal discount) {
//                return new Invoice(
//                                brand,
//                                customerId,
//                                address,
//                                invoiceAddress,
//                                items,
//                                discount);
//        }
//
//        public static InvoiceResponseDTO createInvoiceResponseDTO(
//                        Brand brand,
//                        LocalDate invoiceDate,
//                        UUID customerId,
//                        Address address,
//                        Address invoiceAddress,
//                        List<InvoiceItem> items,
//                        BigDecimal totalSetupFee,
//                        BigDecimal totalMonthlyFee,
//                        BigDecimal discount,
//                        BigDecimal totalAmount) {
//                return new InvoiceResponseDTO(
//                                brand,
//                                invoiceDate,
//                                customerId,
//                                address,
//                                invoiceAddress,
//                                items,
//                                totalSetupFee,
//                                totalMonthlyFee,
//                                discount,
//                                totalAmount);
//        }
//
//        public static Address createAddress(
//                        String street,
//                        String number,
//                        String postcode,
//                        String city,
//                        String country) {
//                return new Address(street, number, postcode, city, country);
//        }
//
//        public static InvoiceItem createInvoiceItem(
//                        UUID productId,
//                        String productName,
//                        UUID contractId,
//                        LocalDate contractCreationDate,
//                        BigDecimal setupFee,
//                        BigDecimal monthlyFee) {
//                return new InvoiceItem(
//                                productId,
//                                productName,
//                                contractId,
//                                contractCreationDate,
//                                setupFee,
//                                monthlyFee);
//        }
//}
