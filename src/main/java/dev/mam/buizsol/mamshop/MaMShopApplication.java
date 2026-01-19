package dev.mam.buizsol.mamshop;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.PartnerProduct;
import dev.mam.buizsol.mamshop.product.model.PremiumMailProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
import dev.mam.buizsol.mamshop.shop.service.ShopService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class MaMShopApplication {

    private MaMShopApplication() {
    }

    public static void main(final String[] args) {
        System.out.println("================================");
        System.out.println("MaM Shop Application Demo Start");
        System.out.println("================================");

        final ShopService shop = ShopService.getInstance();

        registerAllProducts(shop);

        final Customer gmxActive = createAndRegisterCustomer(
                shop,
                Brand.GMX,
                "Active",
                "GMX");
        final Customer gmxInactive = createAndRegisterCustomer(
                shop,
                Brand.GMX,
                "Inactive",
                "GMX");

        final Customer webActive = createAndRegisterCustomer(
                shop,
                Brand.WEB_DE,
                "Active",
                "WEB");
        final Customer webInactive = createAndRegisterCustomer(
                shop,
                Brand.WEB_DE,
                "Inactive",
                "WEB");

        final Customer mailActive = createAndRegisterCustomer(
                shop,
                Brand.MAIL_COM,
                "Active",
                "MAIL");
        final Customer mailInactive = createAndRegisterCustomer(
                shop,
                Brand.MAIL_COM,
                "Inactive",
                "MAIL");

        try {
            shop.activateCustomer(gmxActive.getId());
            shop.activateCustomer(webActive.getId());
            shop.activateCustomer(mailActive.getId());

            shop.updateAddress(
                    gmxActive.getId(),
                    new Address(
                            "Main St",
                            "10",
                            "12345",
                            "Berlin",
                            "Germany"));
            shop.updateInvoiceAddress(
                    gmxActive.getId(),
                    new Address(
                            "Bill St",
                            "5",
                            "54321",
                            "Munich",
                            "Germany"));
            shop.updateCommunicationDetails(
                    gmxActive.getId(),
                    new CommunicationDetails(
                            "gmx.active@example.com",
                            "+49123456789"));

            printProductSummary(shop);

            purchaseProductsForCustomer(
                    shop,
                    gmxActive);
            purchaseProductsForCustomer(
                    shop,
                    webActive);
            purchaseProductsForCustomer(
                    shop,
                    mailActive);

            printContractSummary(
                    shop,
                    gmxActive.getId());

            generateAndPrintInvoice(
                    shop,
                    gmxActive.getId());
            generateAndPrintInvoice(
                    shop,
                    webActive.getId());
            generateAndPrintInvoice(
                    shop,
                    mailActive.getId());

            demonstrateExceptionScenarios(
                    shop,
                    gmxInactive,
                    webActive);

            demonstrateUtilityMethods(
                    shop,
                    mailInactive.getId());
            shop.removeCustomer(webInactive.getId());
            System.out.println("Utility: Also removed webInactive for cleanup.");

        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

        System.out.println("================================");
        System.out.println("MaM Shop Application Demo End");
        System.out.println("================================");
    }

    private static void registerAllProducts(final ShopService shop) {
        for (final Brand brand : Brand.values()) {
            shop.registerProduct(new StandardMailProduct(
                    brand.name() + " Basic Mail",
                    brand,
                    new BigDecimal("0.50")));
            shop.registerProduct(new PremiumMailProduct(
                    brand.name() + " Premium Mail",
                    brand,
                    new BigDecimal("4.99")));
            shop.registerProduct(new PartnerProduct(
                    brand.name() + " Storage Pro",
                    brand,
                    new BigDecimal("9.99"),
                    new BigDecimal("1.99")));
            shop.registerProduct(new PartnerProduct(
                    brand.name() + " Music Streaming",
                    brand,
                    BigDecimal.ZERO,
                    new BigDecimal("9.99")));
            shop.registerProduct(new PartnerProduct(
                    brand.name() + " Security Pack",
                    brand,
                    new BigDecimal("4.99"),
                    new BigDecimal("2.49")));
        }
    }

    private static Customer createAndRegisterCustomer(
            final ShopService shop,
            final Brand brand,
            final String firstName,
            final String lastName) {
        final Address addr = new Address(
                "Street",
                "1",
                "123",
                "City",
                "Country");
        final CommunicationDetails comm = new CommunicationDetails(
                firstName.toLowerCase() + "@example.com",
                "123456");
        final Customer customer = new Customer(
                firstName,
                lastName,
                LocalDate.of(
                        1990,
                        1,
                        1),
                addr,
                null,
                comm,
                brand);
        return shop.registerCustomer(customer);
    }

    private static void printProductSummary(final ShopService shop) {
        System.out.println("Inventory Summary:");
        for (final Brand brand : Brand.values()) {
            final List<Product> products = shop.loadAllProductsForBrand(brand);
            System.out.println("- Brand " + brand + ": " + products.size() + " products.");
        }
    }

    private static void purchaseProductsForCustomer(
            final ShopService shop,
            final Customer customer) {
        final List<Product> products = shop.loadAllProductsForBrand(customer.getBrand());
        for (int i = 0; i < Math.min(
                5,
                products.size()); i++) {
            try {
                final Contract contract = shop.purchaseProduct(
                        customer.getId(),
                        products.get(i).getId());
                shop.activateContract(contract.getId());
            } catch (Exception e) {
                System.err.println("Purchase failed for customer " + customer.getId() + ": " + e.getMessage());
            }
        }
    }

    private static void printContractSummary(
            final ShopService shop,
            final UUID customerId) {
        try {
            final List<Contract> contracts = shop.loadAllContracts(customerId);
            System.out.println("Customer " + customerId + " has " + contracts.size() + " active contracts.");
        } catch (Exception e) {
            System.err.println("Failed to load contracts: " + e.getMessage());
        }
    }

    private static void generateAndPrintInvoice(
            final ShopService shop,
            final UUID customerId) {
        try {
            final Invoice invoice = shop.generateInvoice(customerId);
            System.out.println("--- Invoice for Customer " + customerId + " (" + invoice.getBrand() + ") ---");
            System.out.println("Total Amount: " + invoice.getTotalAmount());
            System.out.println("Items: " + invoice.getItems().size());
            System.out.println("-------------------------------------");
        } catch (Exception e) {
            System.err.println("Failed to generate invoice: " + e.getMessage());
        }
    }

    private static void demonstrateExceptionScenarios(
            final ShopService shop,
            final Customer inactive,
            final Customer mismatchBrand) {
        System.out.println("Demonstrating exception scenarios:");

        try {
            final List<Product> products = shop.loadAllProductsForBrand(inactive.getBrand());
            shop.purchaseProduct(
                    inactive.getId(),
                    products.get(0).getId());
        } catch (Exception e) {
            System.out.println("Expected (Inactive): " + e.getMessage());
        }

        try {
            final Brand otherBrand = mismatchBrand.getBrand() == Brand.GMX ? Brand.WEB_DE : Brand.GMX;
            final List<Product> products = shop.loadAllProductsForBrand(otherBrand);
            shop.purchaseProduct(
                    mismatchBrand.getId(),
                    products.get(0).getId());
        } catch (Exception e) {
            System.out.println("Expected (Brand Mismatch): " + e.getMessage());
        }
    }

    private static void demonstrateUtilityMethods(
            final ShopService shop,
            final UUID customerId) {
        try {
            final Customer loaded = shop.loadCustomer(customerId);
            System.out.println("Utility: Loaded " + loaded.getFirstName());
            shop.removeCustomer(customerId);
            System.out.println("Utility: Removed customer successfully.");
            try {
                shop.loadCustomer(customerId);
            } catch (CustomerNotFoundException e) {
                System.out.println("Utility: Confirmed customer is gone.");
            }
        } catch (Exception e) {
            System.err.println("Utility test failed: " + e.getMessage());
        }
    }
}
