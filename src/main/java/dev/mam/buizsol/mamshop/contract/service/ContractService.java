package dev.mam.buizsol.mamshop.contract.service;

import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.contract.model.ContractStatus;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractService {

        Contract createContract(
                        final Customer customer,
                        final Product product);

        Optional<Contract> findContractById(
                        final UUID id);

        List<Contract> findContractsByCustomerId(
                        final UUID customerId);

        List<Contract> findContractsByProductId(
                        final UUID productId);

        Contract updateContractStatus(
                        final UUID contractId,
                        final ContractStatus status) throws ContractNotFoundException;
}
