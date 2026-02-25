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
                customer.id(),
                customer.firstName(),
                customer.lastName(),
                customer.birthDate(),
                toAddressDTO(customer.address()),
                toAddressDTO(customer.invoiceAddress()),
                toCommunicationDetailsDTO(customer.communicationDetails()),
                customer.brand(),
                customer.status());
    }

    private Address toAddress(final AddressRequestDTO dto) {
        return new Address(
                dto.street(),
                dto.number(),
                dto.postcode(),
                dto.city(),
                dto.country());
    }

    private AddressRequestDTO toAddressDTO(final Address address) {
        return new AddressRequestDTO(
                address.street(),
                address.number(),
                address.postcode(),
                address.city(),
                address.country());
    }

    private CommunicationDetails toCommunicationDetails(final CommunicationDetailsRequestDTO dto) {
        return new CommunicationDetails(
                dto.email(),
                dto.telephone());
    }

    private CommunicationDetailsRequestDTO toCommunicationDetailsDTO(final CommunicationDetails communicationDetails) {
        return new CommunicationDetailsRequestDTO(
                communicationDetails.email(),
                communicationDetails.telephone());
    }
}
