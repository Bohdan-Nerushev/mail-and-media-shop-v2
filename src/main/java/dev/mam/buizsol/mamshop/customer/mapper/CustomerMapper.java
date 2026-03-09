package dev.mam.buizsol.mamshop.customer.mapper;

import dev.mam.buizsol.mamshop.customer.dto.AddressRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.CommunicationDetailsRequestDTO;
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
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getBirthDate(),
                toAddressDTO(customer.getAddress()),
                customer.getInvoiceAddress() != null ? toAddressDTO(customer.getInvoiceAddress()) : null,
                customer.getCommunicationDetails() != null
                        ? toCommunicationDetailsDTO(customer.getCommunicationDetails())
                        : null,
                customer.getBrand(),
                customer.getStatus());
    }

    public Address toAddress(@NotNull final AddressRequestDTO dto) {
        return Address.builder()
                .street(dto.street())
                .number(dto.number())
                .postcode(dto.postcode())
                .city(dto.city())
                .country(dto.country())
                .build();
    }

    public AddressRequestDTO toAddressDTO(@NotNull final Address address) {
        return new AddressRequestDTO(
                address.getStreet(),
                address.getNumber(),
                address.getPostcode(),
                address.getCity(),
                address.getCountry());
    }

    public CommunicationDetails toCommunicationDetails(@NotNull final CommunicationDetailsRequestDTO dto) {
        return CommunicationDetails.builder()
                .email(dto.email())
                .telephone(dto.telephone())
                .build();
    }

    public CommunicationDetailsRequestDTO toCommunicationDetailsDTO(
            @NotNull final CommunicationDetails communicationDetails) {
        return new CommunicationDetailsRequestDTO(
                communicationDetails.getEmail(),
                communicationDetails.getTelephone());
    }
}
