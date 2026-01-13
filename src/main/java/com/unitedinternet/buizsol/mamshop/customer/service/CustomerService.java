package com.unitedinternet.buizsol.mamshop.customer.service;

import com.unitedinternet.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import com.unitedinternet.buizsol.mamshop.customer.model.Address;
import com.unitedinternet.buizsol.mamshop.customer.model.Brand;
import com.unitedinternet.buizsol.mamshop.customer.model.CommunicationDetails;
import com.unitedinternet.buizsol.mamshop.customer.model.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public interface CustomerService {

    @NotNull
    static CustomerService getInstance() {
        return CustomerServiceImpl.getInstance();
    }

    @NotNull
    Customer createCustomer(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull LocalDate birthDate,
            @NotNull Address address,
            Address invoiceAddress,
            @NotNull CommunicationDetails communicationDetails,
            @NotNull Brand brand);

    void updateAddress(@NotNull UUID id, @NotNull Address address) throws CustomerNotFoundException;

    void updateInvoiceAddress(@NotNull UUID id, @NotNull Address address) throws CustomerNotFoundException;

    void updateCommunicationDetails(@NotNull UUID id, @NotNull CommunicationDetails communicationDetails)
            throws CustomerNotFoundException;

    void activateCustomer(@NotNull UUID id) throws CustomerNotFoundException;

    void deactivateCustomer(@NotNull UUID id) throws CustomerNotFoundException;

    void deleteCustomer(@NotNull UUID id) throws CustomerNotFoundException;
}
