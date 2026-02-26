package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    CustomerServiceImpl(
            final CustomerRepository customerRepository) {
        if (customerRepository == null) {
            throw new CustomerValidationException("Customer repository must not be null");
        }
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(
            final Customer customer) {
        if (customer == null) {
            throw new CustomerValidationException("Customer must not be null");
        }
        customerRepository.save(customer);
        return customer;
    }

    @Override
    public void updateAddress(
            final UUID customerId,
            final Address address) throws CustomerNotFoundException {
        if (customerId == null || address == null) {
            throw new CustomerValidationException("Parameters must not be null");
        }
        final Customer customer = customerRepository.getById(customerId);
        customerRepository.update(customer.withAddress(address));
    }

    @Override
    public void updateInvoiceAddress(
            final UUID customerId,
            final Address address) throws CustomerNotFoundException {
        if (customerId == null || address == null) {
            throw new CustomerValidationException("Parameters must not be null");
        }
        final Customer customer = customerRepository.getById(customerId);
        customerRepository.update(customer.withInvoiceAddress(address));
    }

    @Override
    public void updateCommunicationDetails(
            final UUID customerId,
            final CommunicationDetails communicationDetails) throws CustomerNotFoundException {
        if (customerId == null || communicationDetails == null) {
            throw new CustomerValidationException("Parameters must not be null");
        }
        final Customer customer = customerRepository.getById(customerId);
        customerRepository.update(customer.withCommunicationDetails(communicationDetails));
    }

    @Override
    public void activateCustomer(
            final UUID customerId) throws CustomerNotFoundException {
        if (customerId == null) {
            throw new CustomerValidationException("Customer ID must not be null");
        }
        final Customer customer = customerRepository.getById(customerId);
        customerRepository.update(customer.withStatus(CustomerStatus.ACTIVE));
    }

    @Override
    public void deactivateCustomer(
            final UUID customerId) throws CustomerNotFoundException {
        if (customerId == null) {
            throw new CustomerValidationException("Customer ID must not be null");
        }
        final Customer customer = customerRepository.getById(customerId);
        customerRepository.update(customer.withStatus(CustomerStatus.INACTIVE));
    }

    @Override
    public void deleteCustomer(
            final UUID customerId) throws CustomerNotFoundException {
        if (customerId == null) {
            throw new CustomerValidationException("Customer ID must not be null");
        }
        customerRepository.delete(customerId);
    }

    @Override
    public Optional<Customer> findCustomerById(
            final UUID customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public List<Customer> findAllCustomers() {
        return List.copyOf(customerRepository.findAll());
    }
}
