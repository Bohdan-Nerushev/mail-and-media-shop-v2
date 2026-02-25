package dev.mam.buizsol.mamshop.customer.dto;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import java.time.LocalDate;
import java.util.UUID;

public record CustomerResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        AddressRequestDTO address,
        AddressRequestDTO invoiceAddress,
        CommunicationDetailsRequestDTO communicationDetails,
        Brand brand,
        CustomerStatus status) {
}
