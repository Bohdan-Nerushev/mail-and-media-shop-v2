package dev.mam.buizsol.mamshop.customer.model;

import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record Customer(
        @NotNull UUID id,
        @NotBlank @Size(min = 1, max = 100) String firstName,
        @NotBlank @Size(min = 1, max = 100) String lastName,
        @NotNull @Past LocalDate birthDate,
        @NotNull @Valid Address address,
        @NotNull @Valid Address invoiceAddress,
        @NotNull @Valid CommunicationDetails communicationDetails,
        @NotNull Brand brand,
        @NotNull CustomerStatus status) {

    public Customer {
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new CustomerValidationException("First name and last name must not be blank");
        }
        if (birthDate == null || address == null || communicationDetails == null || brand == null) {
            throw new CustomerValidationException("Mandatory fields must not be null");
        }
        if (invoiceAddress == null) {
            invoiceAddress = address;
        }
    }

    public static Customer create(
            @NotBlank final String firstName,
            @NotBlank final String lastName,
            @NotNull @Past final LocalDate birthDate,
            @NotNull @Valid final Address address,
            @Nullable @Valid final Address invoiceAddress,
            @NotNull @Valid final CommunicationDetails communicationDetails,
            @NotNull final Brand brand) {

        return new Customer(
                UUID.randomUUID(),
                firstName,
                lastName,
                birthDate,
                address,
                invoiceAddress != null ? invoiceAddress : address,
                communicationDetails,
                brand,
                CustomerStatus.INACTIVE);
    }

    @NotNull
    public Customer withStatus(@NotNull final CustomerStatus newStatus) {
        if (newStatus == null) {
            throw new CustomerValidationException("Status must not be null");
        }
        return new Customer(id, firstName, lastName, birthDate, address, invoiceAddress, communicationDetails, brand,
                newStatus);
    }

    @NotNull
    public Customer withAddress(@NotNull @Valid final Address newAddress) {
        if (newAddress == null) {
            throw new CustomerValidationException("Address must not be null");
        }
        return new Customer(id, firstName, lastName, birthDate, newAddress, invoiceAddress, communicationDetails, brand,
                status);
    }

    @NotNull
    public Customer withInvoiceAddress(@NotNull @Valid final Address newInvoiceAddress) {
        if (newInvoiceAddress == null) {
            throw new CustomerValidationException("Invoice address must not be null");
        }
        return new Customer(id, firstName, lastName, birthDate, address, newInvoiceAddress, communicationDetails, brand,
                status);
    }

    @NotNull
    public Customer withCommunicationDetails(@NotNull @Valid final CommunicationDetails newDetails) {
        if (newDetails == null) {
            throw new CustomerValidationException("Communication details must not be null");
        }
        return new Customer(id, firstName, lastName, birthDate, address, invoiceAddress, newDetails, brand, status);
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", brand=" + brand + ", status=" + status + '}';
    }
}
