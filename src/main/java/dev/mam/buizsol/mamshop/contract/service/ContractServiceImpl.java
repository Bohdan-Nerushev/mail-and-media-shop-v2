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

final class ContractServiceImpl implements ContractService {

    private final ContractRepository repository;

    private ContractServiceImpl(
            @NotNull final ContractRepository repository) {
        this.repository = repository;
    }

    private static final class Holder {
        private static final ContractServiceImpl INSTANCE = new ContractServiceImpl(ContractRepository.getInstance());
    }

    @NotNull
    static ContractService getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    @NotNull
    public Contract createContract(
            @NotNull final Customer customer,
            @NotNull final Product product) {
        validateNotNull(customer, "Customer");
        validateNotNull(product, "Product");

        Contract contract = new Contract(customer, product);
        return repository.save(contract);
    }

    @Override
    @NotNull
    public Optional<Contract> findContractById(
            @NotNull final UUID id) {
        validateNotNull(id, "Contract ID");
        return repository.findById(id);
    }

    @Override
    @NotNull
    public List<Contract> findContractsByCustomerId(
            @NotNull final UUID customerId) {
        validateNotNull(customerId, "Customer ID");
        return repository.findByCustomerId(customerId);
    }

    @Override
    @NotNull
    public List<Contract> findContractsByProductId(
            @NotNull final UUID productId) {
        validateNotNull(productId, "Product ID");
        return repository.findByProductId(productId);
    }

    @Override
    @NotNull
    public Contract updateContractStatus(
            @NotNull final UUID contractId,
            @NotNull final ContractStatus status)
            throws ContractNotFoundException {
        validateNotNull(contractId, "Contract ID");
        validateNotNull(status, "Status");

        Contract contract = repository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException("Contract with ID " + contractId + " not found"));

        contract.updateStatus(status);
        return repository.update(contract);
    }

    private void validateNotNull(
            final Object value,
            final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
