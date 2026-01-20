package dev.mam.buizsol.mamshop.shop.service;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
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
        Customer loadCustomer(@NotNull final UUID customerId);

        void removeCustomer(@NotNull final UUID customerId);

        void activateCustomer(@NotNull final UUID customerId);

        @NotNull
        Customer updateAddress(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final Address address);

        @NotNull
        Customer updateInvoiceAddress(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final Address invoiceAddress);

        @NotNull
        Customer updateCommunicationDetails(
                        @NotNull final UUID customerId,
                        @NotNull @Valid final CommunicationDetails details);

        @NotNull
        List<Contract> loadAllContracts(@NotNull final UUID customerId);

        @NotNull
        Invoice generateInvoice(@NotNull final UUID customerId);

        void activateContract(@NotNull final UUID contractId);

        @NotNull
        List<Product> loadAllProductsForBrand(@NotNull @Valid final Brand brand);

        @NotNull
        Contract purchaseProduct(
                        @NotNull final UUID customerId,
                        @NotNull final UUID productId);
}
