package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractService {

    @NotNull
    static ContractService getInstance() {
        return ContractServiceImpl.getInstance();
    }

    @NotNull
    Contract createContract(
            @NotNull(message = "Customer must not be null") final Customer customer,
            @NotNull(message = "Product must not be null") final Product product);

    @NotNull
    Optional<Contract> findContractById(
            @NotNull(message = "Contract ID must not be null") final UUID id);

    @NotNull
    List<Contract> findContractsByCustomerId(
            @NotNull(message = "Customer ID must not be null") final UUID customerId);

    @NotNull
    List<Contract> findContractsByProductId(
            @NotNull(message = "Product ID must not be null") final UUID productId);

    @NotNull
    Contract updateContractStatus(
            @NotNull(message = "Contract ID must not be null") final UUID contractId,
            @NotNull(message = "Status must not be null") final ContractStatus status) throws ContractNotFoundException;
}
