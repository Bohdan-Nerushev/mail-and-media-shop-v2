package dev.mam.buizsol.mamshop;

import dev.mam.buizsol.mamshop.billing.model.Invoice;
import dev.mam.buizsol.mamshop.contract.model.Contract;
import dev.mam.buizsol.mamshop.customer.exception.CustomerNotFoundException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.customer.model.CommunicationDetails;
import dev.mam.buizsol.mamshop.customer.model.Customer;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.shop.service.ShopService;

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

                System.out.println("Product catalog initialized automatically.");

                final Customer customerGMXInactive = createCustomer(
                                "Maxim",
                                "Kovalenko",
                                LocalDate.of(1985, 5, 20),
                                createAddress(
                                                "Khreshchatyk St",
                                                "22",
                                                "01001",
                                                "Kyiv",
                                                "Ukraine"),
                                createAddress(
                                                "Instytutska St",
                                                "5",
                                                "01008",
                                                "Kyiv",
                                                "Ukraine"),
                                createCommunicationDetails(
                                                "m.kovalenko@gmx.com",
                                                "+380501234567"),
                                Brand.GMX);

                shop.registerCustomer(customerGMXInactive);

                final Customer customerGMXActive = createCustomer(
                                "Olena",
                                "Petrenko",
                                LocalDate.of(1992, 8, 15),
                                createAddress(
                                                "Volodymyrska St",
                                                "60",
                                                "01033",
                                                "Kyiv",
                                                "Ukraine"),
                                null,
                                createCommunicationDetails(
                                                "o.petrenko@gmx.com",
                                                "+380679876543"),
                                Brand.GMX);
                shop.registerCustomer(customerGMXActive);

                final Customer customerWebDeInactive = createCustomer(
                                "Andriy",
                                "Shevchenko",
                                LocalDate.of(1976, 9, 29),
                                createAddress(
                                                "Sumska St",
                                                "10",
                                                "61002",
                                                "Kharkiv",
                                                "Ukraine"),
                                createAddress(
                                                "Pushkinska St",
                                                "12",
                                                "61057",
                                                "Kharkiv",
                                                "Ukraine"),
                                createCommunicationDetails(
                                                "a.shevchenko@web.de",
                                                "+380631112233"),
                                Brand.WEB_DE);
                shop.registerCustomer(customerWebDeInactive);

                final Customer customerWebDeActive = createCustomer(
                                "Svitlana",
                                "Ivanova",
                                LocalDate.of(1988, 3, 12),
                                createAddress(
                                                "Deribasivska St",
                                                "1",
                                                "65000",
                                                "Odesa",
                                                "Ukraine"),
                                null,
                                createCommunicationDetails(
                                                "s.ivanova@web.de",
                                                "+380995554433"),
                                Brand.WEB_DE);
                shop.registerCustomer(customerWebDeActive);

                final Customer customerMailInactive = createCustomer(
                                "Ihor",
                                "Tkachenko",
                                LocalDate.of(1995, 11, 30),
                                createAddress(
                                                "Soborna St",
                                                "100",
                                                "21000",
                                                "Vinnytsia",
                                                "Ukraine"),
                                createAddress(
                                                "Pirogova St",
                                                "45",
                                                "21018",
                                                "Vinnytsia",
                                                "Ukraine"),
                                createCommunicationDetails(
                                                "i.tkachenko@mail.com",
                                                "+380687778899"),
                                Brand.MAIL_COM);
                shop.registerCustomer(customerMailInactive);

                final Customer customerMailActive = createCustomer(
                                "Nataliia",
                                "Melnyk",
                                LocalDate.of(1983, 7, 4),
                                createAddress(
                                                "Svobody Ave",
                                                "24",
                                                "79000",
                                                "Lviv",
                                                "Ukraine"),
                                null,
                                createCommunicationDetails(
                                                "n.melnyk@mail.com",
                                                "+380932223344"),
                                Brand.MAIL_COM);
                shop.registerCustomer(customerMailActive);

                try {
                        shop.activateCustomer(customerGMXActive.getId());
                        shop.activateCustomer(customerWebDeActive.getId());
                        shop.activateCustomer(customerMailActive.getId());

                        shop.updateAddress(
                                        customerGMXActive.getId(),
                                        new Address(
                                                        "Main St",
                                                        "10",
                                                        "12345",
                                                        "Berlin",
                                                        "Germany"));
                        shop.updateInvoiceAddress(
                                        customerGMXActive.getId(),
                                        new Address(
                                                        "Bill St",
                                                        "5",
                                                        "54321",
                                                        "Munich",
                                                        "Germany"));
                        shop.updateCommunicationDetails(
                                        customerGMXActive.getId(),
                                        new CommunicationDetails(
                                                        "gmx.active@example.com",
                                                        "+49123456789"));

                        printProductSummary(shop);

                        purchaseProductsForCustomer(
                                        shop,
                                        customerGMXActive);
                        purchaseProductsForCustomer(
                                        shop,
                                        customerWebDeActive);
                        purchaseProductsForCustomer(
                                        shop,
                                        customerMailActive);

                        printContractSummary(
                                        shop,
                                        customerGMXActive.getId());

                        generateAndPrintInvoice(
                                        shop,
                                        customerGMXActive.getId());
                        generateAndPrintInvoice(
                                        shop,
                                        customerWebDeActive.getId());
                        generateAndPrintInvoice(
                                        shop,
                                        customerMailActive.getId());

                        demonstrateExceptionScenarios(
                                        shop,
                                        customerGMXInactive,
                                        customerWebDeActive);

                        demonstrateUtilityMethods(
                                        shop,
                                        customerMailActive.getId());
                        shop.deactivateCustomer(customerGMXActive.getId());
                        shop.removeCustomer(customerWebDeActive.getId());
                        System.out.println("Utility: Also removed webInactive for cleanup.");

                } catch (Exception e) {
                        System.err.println("An unexpected error occurred: " + e.getMessage());
                }

                System.out.println("================================");
                System.out.println("MaM Shop Application Demo End");
                System.out.println("================================");
        }

        private static Address createAddress(
                        final String street,
                        final String houseNumber,
                        final String zipCode,
                        final String city,
                        final String country) {
                return new Address(
                                street,
                                houseNumber,
                                zipCode,
                                city,
                                country);
        }

        private static CommunicationDetails createCommunicationDetails(
                        final String email,
                        final String phoneNumber) {
                return new CommunicationDetails(
                                email,
                                phoneNumber);
        }

        private static Customer createCustomer(
                        final String firstName,
                        final String lastName,
                        final LocalDate birthDate,
                        final Address address,
                        final Address invoiceAddress,
                        final CommunicationDetails communicationDetails,
                        final Brand brand) {
                return new Customer(
                                firstName,
                                lastName,
                                birthDate,
                                address,
                                invoiceAddress,
                                communicationDetails,
                                brand);
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
                                System.err.println("Purchase failed for customer " + customer.getId() + ": "
                                                + e.getMessage());
                        }
                }
        }

        private static void printContractSummary(
                        final ShopService shop,
                        final UUID customerId) {
                try {
                        final List<Contract> contracts = shop.loadAllContracts(customerId);
                        System.out.println(
                                        "Customer " + customerId + " has " + contracts.size() + " active contracts.");
                } catch (Exception e) {
                        System.err.println("Failed to load contracts: " + e.getMessage());
                }
        }

        private static void generateAndPrintInvoice(
                        final ShopService shop,
                        final UUID customerId) {
                try {
                        final Invoice invoice = shop.generateInvoice(customerId);
                        System.out.println(
                                        "--- Invoice for Customer " + customerId + " (" + invoice.getBrand() + ") ---");
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
