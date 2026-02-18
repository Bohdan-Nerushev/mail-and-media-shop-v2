package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullCustomer;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
final class CustomerServiceImpl implements CustomerService {

    private static final String CUSTOMER_ID_FIELD_NAME = "Customer ID";
    private static final String CUSTOMER_FIELD_NAME = "Customer";

    private final CustomerRepository customerRepository;

    CustomerServiceImpl(final CustomerRepository customerRepository) {
        validateNotNullCustomer(customerRepository, "Repository");
        this.customerRepository = customerRepository;
    }

    @Override
    @NotNull
    public Customer createCustomer(@Valid @NotNull final Customer customer) {
        validateNotNullCustomer(customer, CUSTOMER_FIELD_NAME);
        customerRepository.save(customer);
        return customer;
    }

    @Override
    public void updateAddress(
            @NotNull final UUID customerId,
            @Valid @NotNull final Address address) throws CustomerNotFoundException {
        validateNotNullCustomer(customerId, CUSTOMER_ID_FIELD_NAME);
        final var customer = customerRepository.getById(customerId);
        customer.setAddress(address);
        customerRepository.update(customer);
    }

    @Override
    public void updateInvoiceAddress(
            @NotNull final UUID customerId,
            @Valid @NotNull final Address address) throws CustomerNotFoundException {
        validateNotNullCustomer(customerId, CUSTOMER_ID_FIELD_NAME);
        final var customer = customerRepository.getById(customerId);
        customer.setInvoiceAddress(address);
        customerRepository.update(customer);
    }

    @Override
    public void updateCommunicationDetails(@NotNull final UUID customerId,
            @Valid @NotNull final CommunicationDetails communicationDetails) throws CustomerNotFoundException {
        validateNotNullCustomer(customerId, CUSTOMER_ID_FIELD_NAME);
        final Customer customer = customerRepository.getById(customerId);
        customer.setCommunicationDetails(communicationDetails);
        customerRepository.update(customer);
    }

    @Override
    public void activateCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException {
        validateNotNullCustomer(customerId, CUSTOMER_ID_FIELD_NAME);
        final Customer customer = customerRepository.getById(customerId);
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepository.update(customer);
    }

    @Override
    public void deactivateCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException {
        validateNotNullCustomer(customerId, CUSTOMER_ID_FIELD_NAME);
        final Customer customer = customerRepository.getById(customerId);
        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.update(customer);
    }

    @Override
    public void deleteCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException {
        validateNotNullCustomer(customerId, CUSTOMER_ID_FIELD_NAME);
        customerRepository.delete(customerId);
    }

    @Override
    @NotNull
    public Optional<Customer> findCustomerById(@NotNull final UUID customerId) {
        validateNotNullCustomer(customerId, CUSTOMER_ID_FIELD_NAME);
        return customerRepository.findById(customerId);
    }

    @Override
    @NotNull
    public List<Customer> findAllCustomers() {
        return List.copyOf(customerRepository.findAll());
    }
}
