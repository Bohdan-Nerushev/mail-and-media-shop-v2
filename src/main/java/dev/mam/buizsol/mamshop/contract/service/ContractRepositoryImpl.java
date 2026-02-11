package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.ContractValidationException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
final class ContractRepositoryImpl implements ContractRepository {

    private final Map<UUID, Contract> storage;

    @Autowired
    ContractRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    @NotNull
    public Contract save(
            @NotNull @Valid final Contract contract) {
        validateNotNull(contract, "Contract");
        storage.put(contract.getId(), contract);
        return contract;
    }

    @Override
    @NotNull
    public Contract update(
            @NotNull @Valid final Contract contract) {
        validateNotNull(contract, "Contract");
        storage.put(contract.getId(), contract);
        return contract;
    }

    @Override
    @NotNull
    public Optional<Contract> findById(
            @NotNull final UUID id) {
        validateNotNull(id, "ID");
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    @NotNull
    public List<Contract> findByCustomerId(
            @NotNull final UUID customerId) {
        validateNotNull(customerId, "Customer ID");
        return storage.values().stream()
                .filter(contract -> contract.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public List<Contract> findByProductId(
            @NotNull final UUID productId) {
        validateNotNull(productId, "Product ID");
        return storage.values().stream()
                .filter(contract -> contract.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public List<Contract> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    private void validateNotNull(
            @Nullable final Object value,
            @NotNull final String fieldName) {
        if (value == null) {
            throw new ContractValidationException(fieldName + " must not be null");
        }
    }
}
