package dev.mam.buizsol.mamshop.contract.model;

import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record Contract(
        @NotNull UUID id,
        @NotNull UUID customerId,
        @NotNull UUID productId,
        @NotNull LocalDate creationDate,
        @NotNull ContractStatus status) {

    public Contract {
        if (id == null || customerId == null || productId == null || creationDate == null || status == null) {
            throw new ContractValidationException("All contract fields must not be null");
        }
    }

    public static Contract create(
            @NotNull @Valid final Customer customer,
            @NotNull @Valid final Product product) {

        if (customer == null || product == null) {
            throw new ContractValidationException("Customer and Product must not be null");
        }
        if (!customer.brand().equals(product.getBrand())) {
            throw new BrandMismatchException(String.format(
                    "Customer brand %s does not match product brand %s",
                    customer.brand(), product.getBrand()));
        }
        if (customer.status() != CustomerStatus.ACTIVE) {
            throw new CustomerNotActiveException("Customer is not active");
        }

        return new Contract(
                UUID.randomUUID(),
                customer.id(),
                product.getId(),
                LocalDate.now(),
                ContractStatus.INACTIVE);
    }

    @NotNull
    public Contract withStatus(@NotNull final ContractStatus newStatus) {
        if (newStatus == null) {
            throw new ContractValidationException("Status must not be null");
        }
        return new Contract(id, customerId, productId, creationDate, newStatus);
    }
}
