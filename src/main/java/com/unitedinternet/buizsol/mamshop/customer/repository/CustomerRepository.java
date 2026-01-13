package com.unitedinternet.buizsol.mamshop.customer.repository;

import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import com.unitedinternet.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    @NotNull
    static CustomerRepository getInstance() {
        return CustomerRepositoryImpl.getInstance();
    }

    void save(
            @NotNull final Customer customer);

    @NotNull
    Optional<Customer> findById(
            @NotNull final UUID id);

    @NotNull
    Collection<Customer> findAll();

    void delete(
            @NotNull final UUID id) throws CustomerNotFoundException;

    void update(
            @NotNull final Customer customer) throws CustomerNotFoundException;
}
