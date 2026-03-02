package dev.mam.buizsol.mamshop.shop.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.contract.exception.BrandMismatchException;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Validated
public interface ShopService {

        @NotNull
        Customer registerCustomer(@NotNull @Valid final Customer customer);

        @NotNull
        Customer loadCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException;

        void removeCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException;

        void activateCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException;

        void deactivateCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException;

        @NotNull
        Customer updateAddress(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final Address address) throws CustomerNotFoundException;

        @NotNull
        Customer updateInvoiceAddress(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final Address invoiceAddress) throws CustomerNotFoundException;

        @NotNull
        Customer updateCommunicationDetails(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final CommunicationDetails details) throws CustomerNotFoundException;

        @NotNull
        List<Contract> loadAllContracts(@NotNull final UUID customerId)
                        throws CustomerNotFoundException;

        @NotNull
        Invoice generateInvoice(@NotNull final UUID customerId)
                        throws CustomerNotFoundException, ProductNotFoundException;

        void activateContract(@NotNull final UUID customerId, @NotNull final UUID contractId)
                        throws CustomerNotFoundException, ContractNotFoundException;

        @NotNull
        List<Product> loadAllProductsForBrand(@NotNull final Brand brand);

        @NotNull
        Contract purchaseProduct(
                        @NotNull final UUID customerId,
                        @NotNull final UUID productId)
                        throws CustomerNotFoundException, ProductNotFoundException, BrandMismatchException;
}
