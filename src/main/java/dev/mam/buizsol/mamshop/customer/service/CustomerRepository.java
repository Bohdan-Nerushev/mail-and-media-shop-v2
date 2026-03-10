package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

        void save(@Valid @NotNull Customer customer);

        @NotNull
        Optional<Customer> findById(@NotNull UUID id);

        @NotNull
        default Customer getById(@NotNull UUID id) throws CustomerNotFoundException {
                return findById(id).orElseThrow(
                                () -> new CustomerNotFoundException("Customer with ID " + id + " not found"));
        }

        @NotNull
        List<Customer> findAll();

        void delete(@NotNull UUID id) throws CustomerNotFoundException;

        void update(@Valid @NotNull Customer customer) throws CustomerNotFoundException;
}
