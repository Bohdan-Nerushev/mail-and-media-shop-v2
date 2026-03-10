package dev.mam.buizsol.mamshop.customer.mapper;

import dev.mam.buizsol.mamshop.customer.dto.AddressRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.AddressResponseDTO;
import dev.mam.buizsol.mamshop.customer.dto.CommunicationDetailsRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.CommunicationDetailsResponseDTO;
import dev.mam.buizsol.mamshop.customer.dto.CustomerRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.CustomerResponseDTO;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toCustomer(@NotNull final CustomerRequestDTO dto) {
        return Customer.create(
                dto.firstName(),
                dto.lastName(),
                dto.birthDate(),
                toAddress(dto.address()),
                dto.invoiceAddress() != null ? toAddress(dto.invoiceAddress()) : null,
                toCommunicationDetails(dto.communicationDetails()),
                dto.brand());
    }

    public CustomerResponseDTO toResponseDTO(@NotNull final Customer customer) {
        return new CustomerResponseDTO(
                customer.id(),
                customer.firstName(),
                customer.lastName(),
                customer.birthDate(),
                toAddressResponseDTO(customer.address()),
                toAddressResponseDTO(customer.invoiceAddress()),
                toCommunicationResponseDTO(customer.communicationDetails()),
                customer.brand(),
                customer.status());
    }

    public Address toAddress(@NotNull final AddressRequestDTO dto) {
        return new Address(
                dto.street(),
                dto.number(),
                dto.postcode(),
                dto.city(),
                dto.country());
    }

    public AddressRequestDTO toAddressDTO(@NotNull final Address address) {
        return new AddressRequestDTO(
                address.street(),
                address.number(),
                address.postcode(),
                address.city(),
                address.country());
    }

    public CommunicationDetails toCommunicationDetails(
            @NotNull final CommunicationDetailsRequestDTO dto) {
        return new CommunicationDetails(
                dto.email(),
                dto.telephone());
    }

    public CommunicationDetailsRequestDTO toCommunicationDetailsDTO(
            @NotNull final CommunicationDetails communicationDetails) {
        return new CommunicationDetailsRequestDTO(
                communicationDetails.email(),
                communicationDetails.telephone());
    }

    public AddressResponseDTO toAddressResponseDTO(
            @NotNull final String street,
            @NotNull final String number,
            @NotNull final String postcode,
            @NotNull final String city,
            @NotNull final String country) {
        return new AddressResponseDTO(
                street,
                number,
                postcode,
                city,
                country);
    }

    public AddressResponseDTO toAddressResponseDTO(@NotNull final Address address) {
        return new AddressResponseDTO(
                address.street(),
                address.number(),
                address.postcode(),
                address.city(),
                address.country());
    }

    public CommunicationDetailsResponseDTO toCommunicationResponseDTO(
            @NotNull final String email,
            @NotNull final String telephone) {
        return new CommunicationDetailsResponseDTO(
                email,
                telephone);
    }

    public CommunicationDetailsResponseDTO toCommunicationResponseDTO(
            @NotNull final CommunicationDetails communicationDetails) {
        return new CommunicationDetailsResponseDTO(
                communicationDetails.email(),
                communicationDetails.telephone());
    }
}
