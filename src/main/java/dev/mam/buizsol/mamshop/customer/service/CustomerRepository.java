package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

        void save(final Customer customer);

        Optional<Customer> findById(final UUID id);

        default Customer getById(final UUID id) throws CustomerNotFoundException {
                return findById(id).orElseThrow(
                        () -> new CustomerNotFoundException("Customer with ID " + id + " not found"));
        }

        List<Customer> findAll();

        void delete(final UUID id) throws CustomerNotFoundException;

        void update(final Customer customer) throws CustomerNotFoundException;
}
