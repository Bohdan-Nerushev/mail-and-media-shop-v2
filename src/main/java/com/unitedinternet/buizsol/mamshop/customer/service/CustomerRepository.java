package com.unitedinternet.buizsol.mamshop.customer.service;

import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import com.unitedinternet.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

interface CustomerRepository {

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
        default Customer getById(@NotNull final UUID id) throws CustomerNotFoundException {
                return findById(id).orElseThrow(
                                () -> new CustomerNotFoundException("Customer with ID " + id + " not found"));
        }

        @NotNull
        Collection<Customer> findAll();

        void delete(
                        @NotNull final UUID id) throws CustomerNotFoundException;

        void update(
                        @NotNull final Customer customer) throws CustomerNotFoundException;
}
