package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateNotNullContract;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
final class ContractServiceImpl implements ContractService {

    private final ContractRepository repository;

    ContractServiceImpl(final ContractRepository repository) {
        this.repository = repository;
    }

    @Override
    @NotNull
    public Contract createContract(
            @NotNull @Valid final Customer customer,
            @NotNull @Valid final Product product) {
        validateNotNullContract(customer, "Customer");
        validateNotNullContract(product, "Product");

        Contract contract = new Contract(customer, product);
        return repository.save(contract);
    }

    @Override
    @NotNull
    public Optional<Contract> findContractById(
            @NotNull final UUID contractId) {
        validateNotNullContract(contractId, "Contract ID");
        return repository.findById(contractId);
    }

    @Override
    @NotNull
    public List<Contract> findContractsByCustomerId(
            @NotNull final UUID customerId) {
        validateNotNullContract(customerId, "Customer ID");
        return List.copyOf(repository.findByCustomerId(customerId));
    }

    @Override
    @NotNull
    public List<Contract> findContractsByProductId(
            @NotNull final UUID productId) {
        validateNotNullContract(productId, "Product ID");
        return List.copyOf(repository.findByProductId(productId));
    }

    @Override
    @NotNull
    public Contract updateContractStatus(
            @NotNull final UUID contractId,
            @NotNull final ContractStatus changeStatus) throws ContractNotFoundException {
        validateNotNullContract(contractId, "Contract ID");
        validateNotNullContract(changeStatus, "Status");

        Contract contract = repository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException("Contract with ID " + contractId + " not found"));

        contract.updateStatus(changeStatus);
        return repository.update(contract);
    }
}
