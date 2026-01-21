package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    @NotNull
    static CustomerService getInstance() {
        return CustomerServiceImpl.getInstance();
    }

    @NotNull
    Customer createCustomer(@Valid @NotNull final Customer customer);

    void updateAddress(@NotNull UUID customerId, @Valid @NotNull Address address);

    void updateInvoiceAddress(@NotNull UUID customerId, @Valid @NotNull Address address);

    void updateCommunicationDetails(@NotNull UUID customerId,
            @Valid @NotNull CommunicationDetails communicationDetails);

    void activateCustomer(@NotNull UUID customerId);

    void deactivateCustomer(@NotNull UUID customerId);

    void deleteCustomer(@NotNull UUID customerId);

    @NotNull
    Optional<Customer> findCustomerById(@NotNull UUID customerId);

    @NotNull
    List<Customer> findAllCustomers();
}
