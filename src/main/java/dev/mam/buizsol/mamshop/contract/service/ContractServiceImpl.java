package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
final class ContractServiceImpl implements ContractService {

    private final ContractRepository repository;

    @Autowired
    ContractServiceImpl(@NotNull final ContractRepository repository) {
        this.repository = repository;
    }

    @Override
    @NotNull
    public Contract createContract(
            @NotNull @Valid final Customer customer,
            @NotNull @Valid final Product product) throws BrandMismatchException {
        validateNotNull(customer, "Customer");
        validateNotNull(product, "Product");

        Contract contract = new Contract(customer, product);
        return repository.save(contract);
    }

    @Override
    @NotNull
    public Optional<Contract> findContractById(
            @NotNull final UUID contractId) {
        validateNotNull(contractId, "Contract ID");
        return repository.findById(contractId);
    }

    @Override
    @NotNull
    public List<Contract> findContractsByCustomerId(
            @NotNull final UUID customerId) {
        validateNotNull(customerId, "Customer ID");
        return List.copyOf(repository.findByCustomerId(customerId));
    }

    @Override
    @NotNull
    public List<Contract> findContractsByProductId(
            @NotNull final UUID productId) {
        validateNotNull(productId, "Product ID");
        return List.copyOf(repository.findByProductId(productId));
    }

    @Override
    @NotNull
    public Contract updateContractStatus(
            @NotNull final UUID contractId,
            @NotNull final ContractStatus changeStatus) throws ContractNotFoundException {
        validateNotNull(contractId, "Contract ID");
        validateNotNull(changeStatus, "Status");

        Contract contract = repository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException("Contract with ID " + contractId + " not found"));

        contract.updateStatus(changeStatus);
        return repository.update(contract);
    }

    private void validateNotNull(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new ContractValidationException(fieldName + " must not be null");
        }
    }
}
