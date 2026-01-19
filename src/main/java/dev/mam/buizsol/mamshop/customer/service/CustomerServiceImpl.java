package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import jakarta.validation.Valid;
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
    public Customer createCustomer(@Valid final Customer customer) {

        validateNotNull(customer, "Customer");
        customerRepository.save(customer);
        return customer;
    }

    @Override
    public void updateAddress(@NotNull final UUID customerId, @Valid final Address address)
            throws CustomerNotFoundException {
        validateNotNull(customerId, "Customer ID");
        final var customer = customerRepository.getById(customerId);
        customer.setAddress(address);
        customerRepository.update(customer);
    }

    @Override
    public void updateInvoiceAddress(@NotNull final UUID customerId, @Valid final Address address)
            throws CustomerNotFoundException {
        validateNotNull(customerId, "Customer ID");
        final var customer = customerRepository.getById(customerId);
        customer.setInvoiceAddress(address);
        customerRepository.update(customer);
    }

    @Override
    public void updateCommunicationDetails(@NotNull final UUID customerId,
            @Valid final CommunicationDetails communicationDetails) throws CustomerNotFoundException {
        validateNotNull(customerId, "Customer ID");
        final Customer customer = customerRepository.getById(customerId);
        customer.setCommunicationDetails(communicationDetails);
        customerRepository.update(customer);
    }

    @Override
    public void activateCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException {
        validateNotNull(customerId, "Customer ID");
        final Customer customer = customerRepository.getById(customerId);
        customer.setStatus(CustomerStatus.ACTIVE);
        ;
        customerRepository.update(customer);
    }

    @Override
    public void deactivateCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException {
        validateNotNull(customerId, "Customer ID");
        final Customer customer = customerRepository.getById(customerId);
        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.update(customer);
    }

    @Override
    public void deleteCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException {
        validateNotNull(customerId, "Customer ID");
        customerRepository.delete(customerId);
    }

    @Override
    @NotNull
    public Optional<Customer> findCustomerById(@NotNull final UUID customerId) {
        validateNotNull(customerId, "Customer ID");
        return customerRepository.findById(customerId);
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
