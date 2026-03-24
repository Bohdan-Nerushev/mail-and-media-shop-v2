package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.model.Contract;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    @NotNull
    List<Contract> findByCustomerId(@NotNull UUID customerId);

    @NotNull
    List<Contract> findByProductId(@NotNull UUID productId);
}
