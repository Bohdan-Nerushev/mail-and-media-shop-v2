package dev.mam.buizsol.mamshop.contract.mapper;

import dev.mam.buizsol.mamshop.contract.dto.ContractResponseDTO;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public ContractResponseDTO toContractResponseDTO(@NotNull Contract contract) {
        return new ContractResponseDTO(
                contract.id(), contract.customerId(), contract.productId(), contract.creationDate(), contract.status());
    }
}
