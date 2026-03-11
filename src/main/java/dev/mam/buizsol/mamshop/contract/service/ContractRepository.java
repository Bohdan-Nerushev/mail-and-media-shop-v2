package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.model.Contract;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepository {

    @NotNull
    Contract save(@NotNull @Valid Contract contract);

    @NotNull
    Contract update(@NotNull @Valid Contract contract);

    @NotNull
    Optional<Contract> findById(@NotNull UUID id);

    @NotNull
    List<Contract> findByCustomerId(@NotNull UUID customerId);

    @NotNull
    List<Contract> findByProductId(@NotNull UUID productId);

    @NotNull
    List<Contract> findAll();
}
