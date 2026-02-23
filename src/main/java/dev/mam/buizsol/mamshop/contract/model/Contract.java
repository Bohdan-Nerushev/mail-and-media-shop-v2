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

public class Contract {

    @NotNull
    private final UUID id;

    @NotNull
    private final UUID customerId;

    @NotNull
    private final UUID productId;

    @NotNull
    private final LocalDate creationDate;

    @NotNull
    private ContractStatus status;

    public Contract(
            @NotNull @Valid final Customer customer,
            @NotNull @Valid final Product product) throws BrandMismatchException {

        if (customer == null || product == null) {
            throw new ContractValidationException("Customer and Product must not be null");
        }
        if (!customer.getBrand().equals(product.getBrand())) {
            throw new BrandMismatchException(String.format(
                    "Customer brand %s does not match product brand %s",
                    customer.getBrand(), product.getBrand()));
        }
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotActiveException("Customer is not active");
        }

        this.id = UUID.randomUUID();
        this.customerId = customer.getId();
        this.productId = product.getId();
        this.creationDate = LocalDate.now();
        this.status = ContractStatus.INACTIVE;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public UUID getCustomerId() {
        return customerId;
    }

    @NotNull
    public UUID getProductId() {
        return productId;
    }

    @NotNull
    public LocalDate getCreationDate() {
        return creationDate;
    }

    @NotNull
    public ContractStatus getStatus() {
        return status;
    }

    public void updateStatus(
            @NotNull final ContractStatus status) {
        if (status == null)
            throw new ContractValidationException("Status must not be null");
        this.status = status;
    }

}
