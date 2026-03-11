package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
class CustomerRepositoryImpl implements CustomerRepository {

    private final Map<UUID, Customer> storage;

    CustomerRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public void save(final Customer customer) {
        if (customer == null) {
            throw new CustomerValidationException("Customer must not be null");
        }
        storage.put(customer.id(), customer);
    }

    @Override
    public Optional<Customer> findById(final UUID id) {
        if (id == null) {
            throw new CustomerValidationException("ID must not be null");
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public void delete(final UUID id) throws CustomerNotFoundException {
        if (id == null) {
            throw new CustomerValidationException("ID must not be null");
        }
        Customer deletedObject = storage.remove(id);
        if (deletedObject == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found");
        }
    }

    @Override
    public void update(final Customer customer) throws CustomerNotFoundException {
        if (customer == null) {
            throw new CustomerValidationException("Customer must not be null");
        }
        if (!storage.containsKey(customer.id())) {
            throw new CustomerNotFoundException("Customer with ID " + customer.id() + " not found");
        }
        storage.put(customer.id(), customer);
    }
}
