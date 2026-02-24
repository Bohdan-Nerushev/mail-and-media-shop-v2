package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.validation.BrandMatch;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Validated
public interface ContractService {

        @NotNull
        @BrandMatch
        Contract createContract(
                        @NotNull @Valid final Customer customer,
                        @NotNull @Valid final Product product);

        @NotNull
        Optional<Contract> findContractById(
                        @NotNull final UUID id);

        @NotNull
        List<Contract> findContractsByCustomerId(
                        @NotNull final UUID customerId);

        @NotNull
        List<Contract> findContractsByProductId(
                        @NotNull final UUID productId);

        @NotNull
        Contract updateContractStatus(
                        @NotNull final UUID contractId,
                        @NotNull final ContractStatus status) throws ContractNotFoundException;

        @NotNull
        List<Contract> findAllContracts();
}
