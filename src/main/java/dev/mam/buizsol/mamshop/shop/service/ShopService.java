package dev.mam.buizsol.mamshop.shop.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.contract.exception.ContractNotFoundException;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotActiveException;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.shop.exception.CustomerAndProductBrandMismatchException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface ShopService {

        @NotNull
        static ShopService getInstance() {
                return ShopServiceImpl.getInstance();
        }

        @NotNull
        Customer registerCustomer(
                        @NotNull @Valid final Customer customer);

        @NotNull
        Product registerProduct(
                        @NotNull @Valid final Product product);

        @NotNull
        Customer loadCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException;

        void removeCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException;

        void activateCustomer(@NotNull final UUID customerId) throws CustomerNotFoundException;

        @NotNull
        Customer updateAddress(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final Address address)
                        throws CustomerNotFoundException, CustomerNotActiveException;

        @NotNull
        Customer updateInvoiceAddress(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final Address invoiceAddress)
                        throws CustomerNotFoundException, CustomerNotActiveException;

        @NotNull
        Customer updateCommunicationDetails(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final CommunicationDetails details)
                        throws CustomerNotFoundException, CustomerNotActiveException;

        @NotNull
        List<Contract> loadAllContracts(@NotNull final UUID customerId)
                        throws CustomerNotFoundException, CustomerNotActiveException;

        @NotNull
        Invoice generateInvoice(@NotNull final UUID customerId)
                        throws CustomerNotFoundException, CustomerNotActiveException;

        void activateContract(@NotNull final UUID contractId) throws ContractNotFoundException;

        @NotNull
        List<Product> loadAllProductsForBrand(@NotNull @Valid final Brand brand);

        @NotNull
        Contract purchaseProduct(
                        @NotNull final UUID customerId,
                        @NotNull final UUID productId)
                        throws CustomerNotFoundException, ProductNotFoundException,
                        CustomerAndProductBrandMismatchException,
                        CustomerNotActiveException;
}
