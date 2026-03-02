package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
final class ContractServiceImpl implements ContractService {

    private final ContractRepository repository;

    ContractServiceImpl(final ContractRepository repository) {
        this.repository = repository;
    }

    @Override
    public Contract createContract(
            final Customer customer,
            final Product product) {
        return repository.save(Contract.create(customer, product));
    }

    @Override
    public Optional<Contract> findContractById(
            final UUID contractId) {
        return repository.findById(contractId);
    }

    @Override
    public List<Contract> findContractsByCustomerId(
            final UUID customerId) {
        return List.copyOf(repository.findByCustomerId(customerId));
    }

    @Override
    public List<Contract> findContractsByProductId(
            final UUID productId) {
        return List.copyOf(repository.findByProductId(productId));
    }

    @Override
    public Contract updateContractStatus(
            final UUID contractId,
            final ContractStatus changeStatus) throws ContractNotFoundException {
        return repository.findById(contractId)
                .map(contract -> repository.update(contract.withStatus(changeStatus)))
                .orElseThrow(() -> new ContractNotFoundException("Contract with ID " + contractId + " not found"));
    }

    @Override
    public List<Contract> findAllContracts() {
        return List.copyOf(repository.findAll());
    }
}
