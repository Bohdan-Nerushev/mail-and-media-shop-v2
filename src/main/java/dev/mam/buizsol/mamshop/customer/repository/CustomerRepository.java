package dev.mam.buizsol.mamshop.customer.repository;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

        @NotNull
        static CustomerRepository getInstance() {
                return CustomerRepositoryImpl.getInstance();
        }

        void save(@Valid final Customer customer);

        @NotNull
        Optional<Customer> findById(
                        @NotNull final UUID id);

        @NotNull
        default Customer getById(@NotNull final UUID id) throws CustomerNotFoundException {
                return findById(id).orElseThrow(
                                () -> new CustomerNotFoundException("Customer with ID " + id + " not found"));
        }

        @NotNull
        Collection<Customer> findAll();

        void delete(@NotNull final UUID id) throws CustomerNotFoundException;

        void update(@Valid final Customer customer) throws CustomerNotFoundException;
}
