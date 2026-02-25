package dev.mam.buizsol.mamshop.contract.controller;

import dev.mam.buizsol.mamshop.config.ErrorResponse;
import dev.mam.buizsol.mamshop.contract.dto.ContractResponseDTO;
import dev.mam.buizsol.mamshop.contract.mapper.ContractMapper;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.shop.service.ShopService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Contract", description = "Contract API")
@RestController
@RequestMapping("/api/v1/customers/{customerId}/contracts")
public class ContractController {

    private final ShopService shopService;
    private final ContractMapper contractMapper;

    public ContractController(final ShopService shopService, final ContractMapper contractMapper) {
        this.shopService = shopService;
        this.contractMapper = contractMapper;
    }

    @Operation(summary = "Load all contracts for a customer", description = "Returns all contracts associated with the specified customer ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts loaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contract.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<ContractResponseDTO> loadAllContractsByCustomerId(@PathVariable @NotNull UUID customerId) {
        log.debug("Loading all contracts for customer: {}", customerId);

        List<Contract> contractList = shopService.loadAllContracts(customerId);
        log.debug("Contract list: {}", contractList);

        List<ContractResponseDTO> contractResponseDTOList = contractList.stream()
                .map(contractMapper::toContractResponseDTO)
                .toList();
        log.debug("Contract response DTO list: {}", contractResponseDTOList);
        log.info("Contracts loaded successfully: {}", contractResponseDTOList);
        return contractResponseDTOList;
    }
}
