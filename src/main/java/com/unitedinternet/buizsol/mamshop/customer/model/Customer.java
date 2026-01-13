package com.unitedinternet.buizsol.mamshop.customer.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Random;

public class Customer {

    @NotNull
    private final Long id;
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
            @NotBlank(message = "First name must not be blank") String firstName,
            @NotBlank(message = "Last name must not be blank") String lastName,
            @NotNull(message = "Birth date must not be null") LocalDate birthDate,
            @NotNull(message = "Address must not be null") Address address,
            @Nullable Address invoiceAddress,
            @NotNull(message = "Communication details must not be null") CommunicationDetails communicationDetails,
            @NotNull(message = "Brand must not be null") Brand brand) {

        this(new Random().nextLong(1, Long.MAX_VALUE), firstName, lastName, birthDate, address, invoiceAddress,
                communicationDetails, brand);
    }


    Customer(
            @NotNull Long id,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull LocalDate birthDate,
            @NotNull Address address,
            @Nullable Address invoiceAddress,
            @NotNull CommunicationDetails communicationDetails,
            @NotNull Brand brand) {

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
    public Long getId() {
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
            @NotNull(message = "Address must not be null") Address address) {
        validateNotNull(address, "Address");
        this.address = address;
    }

    @NotNull
    public Address getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(
            @NotNull(message = "Invoice address must not be null") Address invoiceAddress) {
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

    public void setStatus(
            @NotNull(message = "Status must not be null") CustomerStatus status) {
        validateNotNull(status, "Status");
        this.status = status;
    }


    public boolean hasSameId(
            @NotNull(message = "Other customer must not be null") Customer other) {
        validateNotNull(other, "Other customer");
        return this.id.equals(other.getId());
    }


    public void verifyIdentificationUniqueness(
            @NotNull(message = "Other customer must not be null") Customer other) {
        if (hasSameId(other)) {
            throw new IllegalArgumentException("Duplicate customer ID detected: " + id);
        }
    }

    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", address=" + address +
                ", invoiceAddress=" + invoiceAddress +
                ", communicationDetails=" + communicationDetails +
                ", brand=" + brand +
                ", status=" + status +
                '}';
    }
}
