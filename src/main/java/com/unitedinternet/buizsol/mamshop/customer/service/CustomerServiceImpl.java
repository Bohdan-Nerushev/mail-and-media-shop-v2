package com.unitedinternet.buizsol.mamshop.customer.service;

import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import com.unitedinternet.buizsol.mamshop.customer.model.Address;
import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.customer.model.CommunicationDetails;
import com.unitedinternet.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

final class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private CustomerServiceImpl() {
        this(CustomerRepository.getInstance());
    }

    CustomerServiceImpl(@NotNull final CustomerRepository customerRepository) {
        if (customerRepository == null) {
            throw new IllegalArgumentException("CustomerRepository must not be null");
        }
        this.customerRepository = customerRepository;
    }

    private static final class Holder {
        private static final CustomerServiceImpl INSTANCE = new CustomerServiceImpl();
    }

    @NotNull
    static CustomerService getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    @NotNull
    public Customer createCustomer(
            @NotBlank final String firstName,
            @NotBlank final String lastName,
            @NotNull final LocalDate birthDate,
            @NotNull final Address address,
            final Address invoiceAddress,
            @NotNull final CommunicationDetails communicationDetails,
            @NotNull final Brand brand) {

        final Customer customer = new Customer(
                firstName,
                lastName,
                birthDate,
                address,
                invoiceAddress,
                communicationDetails,
                brand);

        customerRepository.save(customer);
        return customer;
    }

    @Override
    public void updateAddress(@NotNull final UUID id, @NotNull final Address address) throws CustomerNotFoundException {
        validateNotNull(id, "ID");
        final Customer customer = customerRepository.getById(id);
        customer.setAddress(address);
        customerRepository.update(customer);
    }

    @Override
    public void updateInvoiceAddress(@NotNull final UUID id, @NotNull final Address address)
            throws CustomerNotFoundException {
        validateNotNull(id, "ID");
        final Customer customer = customerRepository.getById(id);
        customer.setInvoiceAddress(address);
        customerRepository.update(customer);
    }

    @Override
    public void updateCommunicationDetails(@NotNull final UUID id,
            @NotNull final CommunicationDetails communicationDetails) throws CustomerNotFoundException {
        validateNotNull(id, "ID");
        final Customer customer = customerRepository.getById(id);
        customer.setCommunicationDetails(communicationDetails);
        customerRepository.update(customer);
    }

    @Override
    public void activateCustomer(@NotNull final UUID id) throws CustomerNotFoundException {
        validateNotNull(id, "ID");
        final Customer customer = customerRepository.getById(id);
        customer.activate();
        customerRepository.update(customer);
    }

    @Override
    public void deactivateCustomer(@NotNull final UUID id) throws CustomerNotFoundException {
        validateNotNull(id, "ID");
        final Customer customer = customerRepository.getById(id);
        customer.deactivate();
        customerRepository.update(customer);
    }

    @Override
    public void deleteCustomer(@NotNull final UUID id) throws CustomerNotFoundException {
        validateNotNull(id, "ID");
        customerRepository.delete(id);
    }

    @Override
    @NotNull
    public Optional<Customer> findCustomerById(@NotNull final UUID id) {
        validateNotNull(id, "ID");
        return customerRepository.findById(id);
    }

    @Override
    @NotNull
    public Collection<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    private void validateNotNull(final Object value, final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
