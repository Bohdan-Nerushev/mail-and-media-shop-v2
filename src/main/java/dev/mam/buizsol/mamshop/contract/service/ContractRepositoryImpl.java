package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
class ContractRepositoryImpl implements ContractRepository {

    private final Map<UUID, Contract> storage;

    ContractRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public Contract save(
            final Contract contract) {
        if (contract == null) {
            throw new ContractValidationException("Contract must not be null");
        }
        storage.put(contract.id(), contract);
        return contract;
    }

    @Override
    public Contract update(
            final Contract contract) {
        if (contract == null) {
            throw new ContractValidationException("Contract must not be null");
        }
        storage.put(contract.id(), contract);
        return contract;
    }

    @Override
    public Optional<Contract> findById(
            final UUID id) {
        if (id == null) {
            throw new ContractValidationException("ID must not be null");
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Contract> findByCustomerId(
            final UUID customerId) {
        if (customerId == null) {
            throw new ContractValidationException("Customer ID must not be null");
        }
        return storage.values().stream()
                .filter(contract -> contract.customerId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Contract> findByProductId(
            final UUID productId) {
        if (productId == null) {
            throw new ContractValidationException("Product ID must not be null");
        }
        return storage.values().stream()
                .filter(contract -> contract.productId().equals(productId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Contract> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }
}
