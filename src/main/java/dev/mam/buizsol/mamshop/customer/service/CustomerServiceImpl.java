package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    CustomerServiceImpl(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer createCustomer(final Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void updateAddress(final UUID customerId, final Address address) throws CustomerNotFoundException {
        final Customer customer = customerRepository.getById(customerId);
        customer.setAddress(address);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void updateInvoiceAddress(final UUID customerId, final Address address) throws CustomerNotFoundException {
        final Customer customer = customerRepository.getById(customerId);
        customer.setInvoiceAddress(address);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void updateCommunicationDetails(final UUID customerId, final CommunicationDetails communicationDetails)
            throws CustomerNotFoundException {
        final Customer customer = customerRepository.getById(customerId);
        customer.setCommunicationDetails(communicationDetails);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void activateCustomer(final UUID customerId) throws CustomerNotFoundException {
        final Customer customer = customerRepository.getById(customerId);
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deactivateCustomer(final UUID customerId) throws CustomerNotFoundException {
        final Customer customer = customerRepository.getById(customerId);
        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(final UUID customerId) throws CustomerNotFoundException {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public Optional<Customer> findCustomerById(final UUID customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public List<Customer> findAllCustomers() {
        return List.copyOf(customerRepository.findAll());
    }
}
