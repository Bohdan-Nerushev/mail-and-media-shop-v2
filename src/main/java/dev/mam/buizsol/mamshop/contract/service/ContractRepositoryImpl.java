package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.model.Contract;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

final class ContractRepositoryImpl implements ContractRepository {

    private final Map<UUID, Contract> storage;

    private ContractRepositoryImpl() {
        this.storage = new ConcurrentHashMap<>();
    }

    private static final class Holder {
        private static final ContractRepositoryImpl INSTANCE = new ContractRepositoryImpl();
    }

    @NotNull
    static ContractRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    @NotNull
    public Contract save(
            @NotNull final Contract contract) {
        validateNotNull(contract, "Contract");
        storage.put(contract.getId(), contract);
        return contract;
    }

    @Override
    @NotNull
    public Contract update(
            @NotNull final Contract contract) {
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
            final Object value,
            final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }
}
