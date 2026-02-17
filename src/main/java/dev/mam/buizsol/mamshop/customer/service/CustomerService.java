package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    Customer createCustomer(final Customer customer);

    void updateAddress(UUID customerId, Address address) throws CustomerNotFoundException;

    void updateInvoiceAddress(UUID customerId, Address address)
            throws CustomerNotFoundException;

    void updateCommunicationDetails(
            UUID customerId,
            CommunicationDetails communicationDetails) throws CustomerNotFoundException;

    void activateCustomer(UUID customerId) throws CustomerNotFoundException;

    void deactivateCustomer(UUID customerId) throws CustomerNotFoundException;

    void deleteCustomer(UUID customerId) throws CustomerNotFoundException;

    Optional<Customer> findCustomerById(UUID customerId);

    List<Customer> findAllCustomers();
}
