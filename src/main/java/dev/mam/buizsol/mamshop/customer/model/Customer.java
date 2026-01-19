package dev.mam.buizsol.mamshop.customer.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class Customer {

    @NotNull
    private final UUID id;
    @NotNull
    private final String firstName;
    @NotNull
    private final String lastName;
    @NotNull
    private final LocalDate birthDate;
    @NotNull
    private Address address;
    @NotNull
    private Address invoiceAddress;
    @NotNull
    private CommunicationDetails communicationDetails;
    @NotNull
    private final Brand brand;
    @NotNull
    private CustomerStatus status;

    public Customer(
            @NotBlank final String firstName,
            @NotBlank final String lastName,
            @NotNull final LocalDate birthDate,
            @NotNull final Address address,
            @Nullable final Address invoiceAddress,
            @NotNull final CommunicationDetails communicationDetails,
            @NotNull final Brand brand) {

        validateNotBlank(firstName, "First name");
        validateNotBlank(lastName, "Last name");
        validateNotNull(birthDate, "Birth date");
        validateNotNull(address, "Address");
        validateNotNull(communicationDetails, "Communication details");
        validateNotNull(brand, "Brand");

        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.invoiceAddress = invoiceAddress != null ? invoiceAddress : address;
        this.communicationDetails = communicationDetails;
        this.brand = brand;
        this.status = CustomerStatus.INACTIVE;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getFirstName() {
        return firstName;
    }

    @NotNull
    public String getLastName() {
        return lastName;
    }

    @NotNull
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @NotNull
    public Address getAddress() {
        return address;
    }

    public void setAddress(
            @NotNull final Address address) {
        validateNotNull(address, "Address");
        this.address = address;
    }

    @NotNull
    public Address getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(
            @NotNull final Address invoiceAddress) {
        validateNotNull(invoiceAddress, "Invoice address");
        this.invoiceAddress = invoiceAddress;
    }

    @NotNull
    public CommunicationDetails getCommunicationDetails() {
        return communicationDetails;
    }

    public void setCommunicationDetails(
            @NotNull final CommunicationDetails communicationDetails) {
        validateNotNull(communicationDetails, "Communication details");
        this.communicationDetails = communicationDetails;
    }

    @NotNull
    public Brand getBrand() {
        return brand;
    }

    @NotNull
    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(
            @NotNull final CustomerStatus status) {
        validateNotNull(status, "Status");
        this.status = status;
    }

    private void validateNotBlank(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    private void validateNotNull(final Object value, final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", brand=" + brand +
                ", status=" + status +
                '}';
    }
}
