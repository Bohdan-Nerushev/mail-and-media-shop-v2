package dev.mam.buizsol.mamshop.customer.controller;

import dev.mam.buizsol.mamshop.config.ErrorResponse;
import dev.mam.buizsol.mamshop.contract.dto.ContractResponseDTO;
import dev.mam.buizsol.mamshop.contract.mapper.ContractMapper;

import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.customer.dto.AddressRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.CommunicationDetailsRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.CustomerRequestDTO;
import dev.mam.buizsol.mamshop.customer.dto.CustomerResponseDTO;
import dev.mam.buizsol.mamshop.customer.dto.PurchaseRequestDTO;
import dev.mam.buizsol.mamshop.customer.mapper.CustomerMapper;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.shop.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Slf4j
@Tag(name = "Customer", description = "Customer API")
@RestController
@RequestMapping(value = "/api/v1/customers")
public class CustomerController {

    private final ShopService shopService;
    private final CustomerMapper customerMapper;
    private final ContractMapper contractMapper;

    public CustomerController(
            final ShopService shopService,
            final CustomerMapper customerMapper,
            final ContractMapper contractMapper) {
        this.shopService = shopService;
        this.customerMapper = customerMapper;
        this.contractMapper = contractMapper;
    }

    @Operation(
            summary = "Register a new customer",
            description = "Creates a new customer with the given data.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid customer data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public @NotNull CustomerResponseDTO registerCustomer(
            @RequestBody @Valid final CustomerRequestDTO customerRequestDTO) {
        log.debug("Registering customer: {}", customerRequestDTO);

        Customer customer = customerMapper.toCustomer(customerRequestDTO);
        log.debug("Customer: {}", customer);

        Customer registeredCustomer = shopService.registerCustomer(customer);
        log.debug("Registered customer: {}", registeredCustomer);

        CustomerResponseDTO customerResponseDTO = customerMapper.toResponseDTO(registeredCustomer);
        log.debug("Customer response DTO: {}", customerResponseDTO);
        log.info("Customer registered successfully: {}", customerResponseDTO);
        return customerResponseDTO;
    }

    @Operation(
            summary = "Load a customer by ID",
            description = "Returns the customer with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer loaded successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/{customerId}")
    public @NotNull CustomerResponseDTO loadCustomer(
            @PathVariable(value = "customerId") @NotNull final UUID customerId) {
        log.debug("Loading customer: {}", customerId);

        Customer customer = shopService.loadCustomer(customerId);
        log.debug("Customer: {}", customer);

        CustomerResponseDTO customerResponseDTO = customerMapper.toResponseDTO(customer);
        log.debug("Customer response DTO: {}", customerResponseDTO);
        log.info("Customer loaded successfully: {}", customerResponseDTO);
        return customerResponseDTO;
    }

    @Operation(summary = "Remove a customer by ID", description = "Changes the status of the specified customer to REMOVED.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Customer removed successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/{customerId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeCustomer(
            @PathVariable(value = "customerId") @NotNull final UUID customerId) {
        log.debug("Removing customer: {}", customerId);
        shopService.removeCustomer(customerId);
        log.info("Customer removed successfully: {}", customerId);
    }

    @Operation(
            summary = "Activate a customer by ID",
            description = "Changes the status of the specified customer to ACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Customer activated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{customerId}/activate")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void activateCustomer(
            @PathVariable(value = "customerId") @NotNull final UUID customerId) {
        log.debug("Activating customer: {}", customerId);

        shopService.activateCustomer(customerId);
        log.info("Customer activated successfully: {}", customerId);
    }

    @Operation(
            summary = "Deactivate a customer by ID",
            description = "Changes the status of the specified customer to INACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Customer deactivated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{customerId}/deactivate")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deactivateCustomer(
            @PathVariable(value = "customerId") @NotNull final UUID customerId) {
        log.debug("Deactivating customer: {}", customerId);
        shopService.deactivateCustomer(customerId);
        log.info("Customer deactivated successfully: {}", customerId);
    }

    @Operation(
            summary = "Update Address of Customer by id",
            description = "Updates the address of the specified customer.")
    @ApiResponses(
            value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Address updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{customerId}/address")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateAddress(
            @PathVariable(value = "customerId") @NotNull final UUID customerId,
            @RequestBody @Valid final AddressRequestDTO addressRequestDTO) {
        log.debug("Updating address for customer: {}", customerId);
        Address address = customerMapper.toAddress(addressRequestDTO);
        log.debug("New Address: {}", address);
        shopService.updateAddress(customerId, address);
        log.info("Address updated successfully: {}", customerId);
    }

    @Operation(
            summary = "Update Invoice Address of Customer by id",
            description = "Updates the invoice address of the specified customer.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Invoice address updated successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{customerId}/invoice-address")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateInvoiceAddress(
            @PathVariable(value = "customerId") @NotNull final UUID customerId,
            @RequestBody @Valid final AddressRequestDTO addressRequestDTO) {
        log.debug("Updating invoice address for customer: {}", customerId);
        Address address = customerMapper.toAddress(addressRequestDTO);
        log.debug("New Invoice Address: {}", address);
        shopService.updateInvoiceAddress(customerId, address);
        log.info("Invoice address updated successfully: {}", customerId);
    }

    @Operation(
            summary = "Update Communication Details of Customer by id",
            description = "Updates the communication details of the specified customer.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Communication details updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{customerId}/communication-details")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateCommunicationDetails(
            @PathVariable(value = "customerId") @NotNull final UUID customerId,
            @RequestBody @Valid final CommunicationDetailsRequestDTO communicationDetailsRequestDTO) {
        log.debug("Updating communication details for customer: {}", customerId);
        CommunicationDetails communicationDetails = customerMapper
                .toCommunicationDetails(communicationDetailsRequestDTO);
        log.debug("New Communication Details: {}", communicationDetails);
        shopService.updateCommunicationDetails(customerId, communicationDetails);
        log.info("Communication details updated successfully: {}", customerId);
    }

    @Operation(
            summary = "Purchase a product",
            description = "Creates a new contract for the specified customer and product.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product purchased successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer or product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid purchase data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/{customerId}/purchases")
    @ResponseStatus(value = HttpStatus.CREATED)
    public @NotNull ContractResponseDTO purchaseProduct(
            @PathVariable(value = "customerId") @NotNull final UUID customerId,
            @RequestBody @Valid final PurchaseRequestDTO request) {
        log.debug("Purchasing product for customer: {}", customerId);

        Contract contract = shopService.purchaseProduct(customerId, request.productId());
        log.debug("Contract: {}", contract);

        ContractResponseDTO contractResponseDTO = contractMapper.toContractResponseDTO(contract);
        log.debug("Contract response DTO: {}", contractResponseDTO);
        log.info("Product purchased successfully: {}", contractResponseDTO);
        return contractResponseDTO;
    }
}
