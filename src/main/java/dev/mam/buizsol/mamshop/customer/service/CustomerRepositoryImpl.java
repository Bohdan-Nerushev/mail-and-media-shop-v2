package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class CustomerRepositoryImpl implements CustomerRepository {

    private final Map<UUID, Customer> storage;

    private CustomerRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    private static final class Holder {
        private static final CustomerRepositoryImpl INSTANCE = new CustomerRepositoryImpl();
    }

    @NotNull
    static CustomerRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void save(
            @Valid @NotNull final Customer customer) {
        validateNotNull(customer, "Customer");
        storage.put(customer.getId(), customer);
    }

    @Override
    @NotNull
    public Optional<Customer> findById(
            @NotNull final UUID id) {
        validateNotNull(id, "ID");
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    @NotNull
    public List<Customer> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public void delete(
            @NotNull final UUID id) throws CustomerNotFoundException {
        validateNotNull(id, "ID");
        Customer deletedObject = storage.remove(id);
        if (deletedObject == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found");
        }
    }

    @Override
    public void update(
            @Valid @NotNull final Customer customer) throws CustomerNotFoundException {
        validateNotNull(customer, "Customer");
        if (!storage.containsKey(customer.getId())) {
            throw new CustomerNotFoundException("Customer with ID " + customer.getId() + " not found");
        }
        storage.put(customer.getId(), customer);
    }

    private void validateNotNull(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new CustomerValidationException(fieldName + " must not be null");
        }
    }

}
