package dev.mam.buizsol.mamshop.customer.model;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.UUID;

public class Customer {

    @NotNull
    private final UUID id;
    @Size(min = 1, max = 100)
    @NotBlank
    private final String firstName;
    @Size(min = 1, max = 100)
    @NotBlank
    private final String lastName;
    @NotNull
    private final LocalDate birthDate;
    @NotNull
    @Valid
    private Address address;
    @NotNull
    @Valid
    private Address invoiceAddress;
    @NotNull
    @Valid
    private CommunicationDetails communicationDetails;
    @NotNull
    private final Brand brand;
    @NotNull
    private CustomerStatus status;

    public Customer(
            @NotBlank final String firstName,
            @NotBlank final String lastName,
            @NotNull @Past final LocalDate birthDate,
            @NotNull @Valid final Address address,
            @Nullable @Valid final Address invoiceAddress,
            @NotNull @Valid final CommunicationDetails communicationDetails,
            @NotNull final Brand brand) {

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new CustomerValidationException("First name and last name must not be blank");
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now()) || address == null || communicationDetails == null
                || brand == null) {
            throw new CustomerValidationException("Mandatory fields must not be null");
        }
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new CustomerValidationException("First name and last name must not be blank");
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now()) || address == null || communicationDetails == null
                || brand == null) {
            throw new CustomerValidationException("Mandatory fields must not be null");
        }

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
            @NotNull @Valid final Address address) {
        this.address = address;
    }

    @NotNull
    public Address getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(
            @NotNull @Valid final Address invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    @NotNull
    public CommunicationDetails getCommunicationDetails() {
        return communicationDetails;
    }

    public void setCommunicationDetails(
            @NotNull @Valid final CommunicationDetails communicationDetails) {
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
        this.status = status;
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
