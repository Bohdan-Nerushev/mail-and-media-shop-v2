//package dev.mam.buizsol.mamshop.customer.controller;
//
//import dev.mam.buizsol.mamshop.customer.dto.AddressRequestDTO;
//import dev.mam.buizsol.mamshop.customer.dto.CommunicationDetailsRequestDTO;
//import dev.mam.buizsol.mamshop.customer.dto.CustomerRequestDTO;
//import dev.mam.buizsol.mamshop.customer.dto.CustomerResponseDTO;
//import dev.mam.buizsol.mamshop.customer.model.Address;
//import dev.mam.buizsol.mamshop.customer.model.Brand;
//import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
//import dev.mam.buizsol.mamshop.customer.model.Customer;
//import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
//
//import dev.mam.buizsol.mamshop.contract.dto.ContractResponseDTO;
//import dev.mam.buizsol.mamshop.contract.model.Contract;
//import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
//import dev.mam.buizsol.mamshop.customer.dto.PurchaseRequestDTO;
//import java.time.LocalDate;
//import java.util.UUID;
//
//public final class CustomerTestFactory {
//
//    private CustomerTestFactory() {
//    }
//
//    public static CustomerRequestDTO createCustomerRequestDTO(
//            String firstName,
//            String lastName,
//            LocalDate birthDate,
//            AddressRequestDTO address,
//            AddressRequestDTO invoiceAddress,
//            CommunicationDetailsRequestDTO communicationDetails,
//            Brand brand) {
//        return new CustomerRequestDTO(
//                firstName,
//                lastName,
//                birthDate,
//                address,
//                invoiceAddress,
//                communicationDetails,
//                brand);
//    }
//
//    public static AddressRequestDTO createAddressRequestDTO(
//            String street,
//            String number,
//            String postcode,
//            String city,
//            String country) {
//        return new AddressRequestDTO(street, number, postcode, city, country);
//    }
//
//    public static CommunicationDetailsRequestDTO createCommunicationDetailsRequestDTO(
//            String email,
//            String telephone) {
//        return new CommunicationDetailsRequestDTO(email, telephone);
//    }
//
//    public static Customer createCustomer(
//            UUID id,
//            String firstName,
//            String lastName,
//            LocalDate birthDate,
//            Address address,
//            Address invoiceAddress,
//            CommunicationDetails communicationDetails,
//            Brand brand,
//            CustomerStatus status) {
//        return new Customer(
//                id,
//                firstName,
//                lastName,
//                birthDate,
//                address,
//                invoiceAddress,
//                communicationDetails,
//                brand,
//                status);
//    }
//
//    public static Address createAddress(
//            String street,
//            String number,
//            String postcode,
//            String city,
//            String country) {
//        return new Address(street, number, postcode, city, country);
//    }
//
//    public static CommunicationDetails createCommunicationDetails(
//            String email,
//            String telephone) {
//        return new CommunicationDetails(email, telephone);
//    }
//
//    public static CustomerResponseDTO createCustomerResponseDTO(
//            UUID id,
//            String firstName,
//            String lastName,
//            LocalDate birthDate,
//            AddressRequestDTO address,
//            AddressRequestDTO invoiceAddress,
//            CommunicationDetailsRequestDTO communicationDetails,
//            Brand brand,
//            CustomerStatus status) {
//        return new CustomerResponseDTO(
//                id,
//                firstName,
//                lastName,
//                birthDate,
//                address,
//                invoiceAddress,
//                communicationDetails,
//                brand,
//                status);
//    }
//
//    public static PurchaseRequestDTO createPurchaseRequestDTO(UUID productId) {
//        return new PurchaseRequestDTO(productId);
//    }
//
//    public static Contract createContract(
//            UUID id,
//            UUID customerId,
//            UUID productId,
//            LocalDate creationDate,
//            ContractStatus status) {
//        return new Contract(id, customerId, productId, creationDate, status);
//    }
//
//    public static ContractResponseDTO createContractResponseDTO(
//            UUID id,
//            UUID customerId,
//            UUID productId,
//            LocalDate creationDate,
//            ContractStatus status) {
//        return new ContractResponseDTO(id, customerId, productId, creationDate, status);
//    }
//}
