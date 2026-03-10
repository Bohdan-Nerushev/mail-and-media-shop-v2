package dev.mam.buizsol.mamshop.contract.controller;

import dev.mam.buizsol.mamshop.config.ErrorResponse;
import dev.mam.buizsol.mamshop.contract.dto.ContractResponseDTO;
import dev.mam.buizsol.mamshop.contract.mapper.ContractMapper;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.shop.service.ShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Contract", description = "Contract API")
@RestController
@RequestMapping(value = "/api/v1/contracts")
public class ContractController {

        private final ShopService shopService;
        private final ContractMapper contractMapper;

        public ContractController(
                        final ShopService shopService,
                        final ContractMapper contractMapper) {
                this.shopService = shopService;
                this.contractMapper = contractMapper;
        }

        @Operation(summary = "Load all contracts for a customer", description = "Returns all contracts associated with the specified customer ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contracts loaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContractResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping(value = "/{customerId}")
        public @NotNull List<ContractResponseDTO> loadAllContractsByCustomerId(
                        @PathVariable(value = "customerId") @NotNull final UUID customerId) {
                log.debug("Loading all contracts for customer: {}", customerId);

                final List<Contract> contractList = shopService.loadAllContracts(customerId);
                log.debug("Contract list: {}", contractList);

                final List<ContractResponseDTO> contractResponseDTOList = contractList.stream()
                                .map(contractMapper::toContractResponseDTO)
                                .toList();
                log.debug("Contract response DTO list: {}", contractResponseDTOList);

                log.info("Contracts loaded successfully: {}", contractResponseDTOList);
                return contractResponseDTOList;
        }

        @Operation(summary = "Activate a contract by ID", description = "Changes the status of the specified contract to ACTIVE.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Contract activated successfully"),
                        @ApiResponse(responseCode = "404", description = "Contract or customer not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Brand mismatch: contract does not belong to the specified customer", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PutMapping(value = "/{contractId}/{customerId}/activate")
        @ResponseStatus(value = HttpStatus.NO_CONTENT)
        public void activateContract(
                        @PathVariable(value = "customerId") @NotNull final UUID customerId,
                        @PathVariable(value = "contractId") @NotNull final UUID contractId) {
                log.debug("Activating contract: {} for customer: {}", contractId, customerId);

                shopService.activateContract(customerId, contractId);
                log.info("Contract activated successfully: {}", contractId);
        }
}
