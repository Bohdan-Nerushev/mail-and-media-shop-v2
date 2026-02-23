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

        Customer registerCustomer(final Customer customer);

        Customer loadCustomer(final UUID customerId) throws CustomerNotFoundException;

        void removeCustomer(final UUID customerId) throws CustomerNotFoundException;

        void activateCustomer(final UUID customerId) throws CustomerNotFoundException;

        void deactivateCustomer(final UUID customerId) throws CustomerNotFoundException;

        Customer updateAddress(
                        final UUID customerId,
                        final Address address) throws CustomerNotFoundException;

        Customer updateInvoiceAddress(
                        final UUID customerId,
                        final Address invoiceAddress) throws CustomerNotFoundException;

        Customer updateCommunicationDetails(
                        final UUID customerId,
                        final CommunicationDetails details) throws CustomerNotFoundException;

        @NotNull
        List<Contract> loadAllContracts(@NotNull final UUID customerId)
                        throws CustomerNotFoundException;

        Invoice generateInvoice(final UUID customerId)
                        throws CustomerNotFoundException, ProductNotFoundException;

        void activateContract(final UUID contractId) throws ContractNotFoundException;

        @NotNull
        List<Product> loadAllProductsForBrand(@NotNull final Brand brand);

        Contract purchaseProduct(
                        final UUID customerId,
                        final UUID productId)
                        throws CustomerNotFoundException, ProductNotFoundException, BrandMismatchException;
}
