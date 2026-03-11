package dev.mam.buizsol.mamshop.customer.service;

import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @NotNull
    default Customer getById(@NotNull final UUID id) throws CustomerNotFoundException {
        return findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found"));
    }
}
