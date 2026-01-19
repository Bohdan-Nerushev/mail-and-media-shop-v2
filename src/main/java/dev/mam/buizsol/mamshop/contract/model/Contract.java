package dev.mam.buizsol.mamshop.contract.model;

import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.customer.model.CustomerStatus;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.annotation.Nullable;
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
            @NotNull final Customer customer,
            @NotNull final Product product) {

        validateNotNull(customer, "Customer");
        validateNotNull(product, "Product");
        validateBrandMatch(customer, product);
        validateCustomerActive(customer);

        this.id = UUID.randomUUID();
        this.customerId = customer.getId();
        this.productId = product.getId();
        this.creationDate = LocalDate.now();
        this.status = ContractStatus.ACTIVE;
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

    public void activate() {
        this.status = ContractStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ContractStatus.INACTIVE;
    }

    public void updateStatus(
            @NotNull final ContractStatus status) {
        validateNotNull(status, "Status");
        this.status = status;
    }

    private void validateBrandMatch(
            final Customer customer,
            final Product product) {
        if (!customer.getBrand().equals(product.getBrand())) {
            throw new BrandMismatchException(String.format(
                    "Customer brand %s does not match product brand %s",
                    customer.getBrand(),
                    product.getBrand()));
        }
    }

    private void validateCustomerActive(
            final Customer customer) {
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotActiveException(String.format(
                    "Customer %s is not active",
                    customer.getId()));
        }
    }

    private void validateNotNull(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
