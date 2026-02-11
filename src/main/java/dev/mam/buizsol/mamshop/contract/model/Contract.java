package dev.mam.buizsol.mamshop.contract.model;

import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateBrandMatch;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateCustomerActive;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullContract;

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

        validateNotNullContract(customer, "Customer");
        validateNotNullContract(product, "Product");
        validateBrandMatch(customer, product);
        validateCustomerActive(customer);

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
        validateNotNullContract(status, "Status");
        this.status = status;
    }

}
