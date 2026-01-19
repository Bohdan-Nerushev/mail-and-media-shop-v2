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
                        @NotNull(message = "Contract must not be null") final Contract contract);

        @NotNull
        Contract update(
                        @NotNull(message = "Contract must not be null") final Contract contract);

        @NotNull
        Optional<Contract> findById(
                        @NotNull(message = "ID must not be null") final UUID id);

        @NotNull
        List<Contract> findByCustomerId(
                        @NotNull(message = "Customer ID must not be null") final UUID customerId);

        @NotNull
        List<Contract> findByProductId(
                        @NotNull(message = "Product ID must not be null") final UUID productId);

        @NotNull
        List<Contract> findAll();
}
