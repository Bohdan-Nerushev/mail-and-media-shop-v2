package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullCustomer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
final class CustomerRepositoryImpl implements CustomerRepository {

    private final Map<UUID, Customer> storage;

    CustomerRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public void save(
            @Valid @NotNull final Customer customer) {
        validateNotNullCustomer(customer, "Customer");
        storage.put(customer.getId(), customer);
    }

    @Override
    @NotNull
    public Optional<Customer> findById(
            @NotNull final UUID id) {
        validateNotNullCustomer(id, "ID");
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
        validateNotNullCustomer(id, "ID");
        Customer deletedObject = storage.remove(id);
        if (deletedObject == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found");
        }
    }

    @Override
    public void update(
            @Valid @NotNull final Customer customer) throws CustomerNotFoundException {
        validateNotNullCustomer(customer, "Customer");
        if (!storage.containsKey(customer.getId())) {
            throw new CustomerNotFoundException("Customer with ID " + customer.getId() + " not found");
        }
        storage.put(customer.getId(), customer);
    }
}
