package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.model.Contract;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ContractRepository {

        @NotNull
        static ContractRepository getInstance() {
                return ContractRepositoryImpl.getInstance();
        }

        @NotNull
        Contract save(
                        @NotNull final Contract contract);

        @NotNull
        Contract update(
                        @NotNull final Contract contract);

        @NotNull
        Optional<Contract> findById(
                        @NotNull final UUID id);

        @NotNull
        List<Contract> findByCustomerId(
                        @NotNull final UUID customerId);

        @NotNull
        List<Contract> findByProductId(
                        @NotNull final UUID productId);

        @NotNull
        List<Contract> findAll();
}
