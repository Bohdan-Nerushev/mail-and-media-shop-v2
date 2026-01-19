package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    @NotNull
    static CustomerService getInstance() {
        return CustomerServiceImpl.getInstance();
    }

    @NotNull
    Customer createCustomer(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull LocalDate birthDate,
            @Valid Address address,
            @Valid Address invoiceAddress,
            @NotNull CommunicationDetails communicationDetails,
            @NotNull Brand brand);

    void updateAddress(@NotNull UUID id, @NotNull Address address) throws CustomerNotFoundException;

    void updateInvoiceAddress(@NotNull UUID id, @NotNull Address address) throws CustomerNotFoundException;

    void updateCommunicationDetails(@NotNull UUID id, @NotNull CommunicationDetails communicationDetails)
            throws CustomerNotFoundException;

    void activateCustomer(@NotNull UUID id) throws CustomerNotFoundException;

    void deactivateCustomer(@NotNull UUID id) throws CustomerNotFoundException;

    void deleteCustomer(@NotNull UUID id) throws CustomerNotFoundException;

    @NotNull
    Optional<Customer> findCustomerById(@NotNull UUID id);

    @NotNull
    Collection<Customer> findAllCustomers();

}
