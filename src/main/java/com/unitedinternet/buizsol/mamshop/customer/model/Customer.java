package com.unitedinternet.buizsol.mamshop.customer.model;

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
    private final CommunicationDetails communicationDetails;
    @NotNull
    private final Brand brand;
    @NotNull
    private CustomerStatus status;

    public Customer(
            @NotBlank(message = "First name must not be blank") final String firstName,
            @NotBlank(message = "Last name must not be blank") final String lastName,
            @NotNull(message = "Birth date must not be null") final LocalDate birthDate,
            @NotNull(message = "Address must not be null") final Address address,
            @Nullable final Address invoiceAddress,
            @NotNull(message = "Communication details must not be null") final CommunicationDetails communicationDetails,
            @NotNull(message = "Brand must not be null") final Brand brand) {

        this(UUID.randomUUID(), firstName, lastName, birthDate, address, invoiceAddress,
                communicationDetails, brand);
    }

    private Customer(
            @NotNull final UUID id,
            @NotBlank final String firstName,
            @NotBlank final String lastName,
            @NotNull final LocalDate birthDate,
            @NotNull final Address address,
            @Nullable final Address invoiceAddress,
            @NotNull final CommunicationDetails communicationDetails,
            @NotNull final Brand brand) {

        validateNotNull(id, "ID");
        validateNotBlank(firstName, "First name");
        validateNotBlank(lastName, "Last name");
        validateNotNull(birthDate, "Birth date");
        validateNotNull(address, "Address");
        validateNotNull(communicationDetails, "Communication details");
        validateNotNull(brand, "Brand");

        this.id = id;
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

    void setAddress(
            @NotNull(message = "Address must not be null") final Address address) {
        validateNotNull(address, "Address");
        this.address = address;
    }

    @NotNull
    public Address getInvoiceAddress() {
        return invoiceAddress;
    }

    void setInvoiceAddress(
            @NotNull(message = "Invoice address must not be null") final Address invoiceAddress) {
        validateNotNull(invoiceAddress, "Invoice address");
        this.invoiceAddress = invoiceAddress;
    }

    @NotNull
    public CommunicationDetails getCommunicationDetails() {
        return communicationDetails;
    }

    @NotNull
    public Brand getBrand() {
        return brand;
    }

    @NotNull
    public CustomerStatus getStatus() {
        return status;
    }

    void setStatus(
            @NotNull(message = "Status must not be null") final CustomerStatus status) {
        validateNotNull(status, "Status");
        this.status = status;
    }

    public void activate() {
        this.status = CustomerStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }

    public boolean hasSameId(
            @NotNull(message = "Other customer must not be null") final Customer other) {
        validateNotNull(other, "Other customer");
        return this.id.equals(other.getId());
    }

    public void verifyIdentificationUniqueness(
            @NotNull(message = "Other customer must not be null") final Customer other) {
        if (hasSameId(other)) {
            throw new IllegalArgumentException("Duplicate customer ID detected: " + id);
        }
    }

    static Customer createForTesting(
            @NotNull final UUID id,
            @NotBlank final String firstName,
            @NotBlank final String lastName,
            @NotNull final LocalDate birthDate,
            @NotNull final Address address,
            @Nullable final Address invoiceAddress,
            @NotNull final CommunicationDetails communicationDetails,
            @NotNull final Brand brand) {
        return new Customer(id, firstName, lastName, birthDate, address, invoiceAddress,
                communicationDetails, brand);
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
