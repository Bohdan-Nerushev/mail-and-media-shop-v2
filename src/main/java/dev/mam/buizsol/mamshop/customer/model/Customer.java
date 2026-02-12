package dev.mam.buizsol.mamshop.customer.model;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotBlankCustomer;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullCustomer;

import java.time.LocalDate;
import java.util.UUID;

public class Customer {

    @NotNull
    private final UUID id;
    @NotBlank
    private final String firstName;
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
            @NotNull final LocalDate birthDate,
            @NotNull @Valid final Address address,
            @Nullable @Valid final Address invoiceAddress,
            @NotNull @Valid final CommunicationDetails communicationDetails,
            @NotNull final Brand brand) {

        validateNotBlankCustomer(firstName, "First name");
        validateNotBlankCustomer(lastName, "Last name");
        validateNotNullCustomer(birthDate, "Birth date");
        validateNotNullCustomer(address, "Address");
        validateNotNullCustomer(communicationDetails, "Communication details");
        validateNotNullCustomer(brand, "Brand");

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
        validateNotNullCustomer(address, "Address");
        this.address = address;
    }

    @NotNull
    public Address getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(
            @NotNull @Valid final Address invoiceAddress) {
        validateNotNullCustomer(invoiceAddress, "Invoice address");
        this.invoiceAddress = invoiceAddress;
    }

    @NotNull
    public CommunicationDetails getCommunicationDetails() {
        return communicationDetails;
    }

    public void setCommunicationDetails(
            @NotNull @Valid final CommunicationDetails communicationDetails) {
        validateNotNullCustomer(communicationDetails, "Communication details");
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
        validateNotNullCustomer(status, "Status");
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
