package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.model.Contract;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepository {

        Contract save(final Contract contract);

        Contract update(final Contract contract);

        Optional<Contract> findById(final UUID id);

        List<Contract> findByCustomerId(final UUID customerId);

        List<Contract> findByProductId(final UUID productId);

        List<Contract> findAll();
}
