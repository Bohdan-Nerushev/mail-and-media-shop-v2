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
import lombok.extern.slf4j.Slf4j;
import dev.mam.buizsol.mamshop.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
public class MaMShopApplication {

        public static void main(final String[] args) throws Exception {
                try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
                        final ShopService shop = context.getBean(ShopService.class);

                        log.info("================================");
                        log.info("MaM Shop Application Demo Start");
                        log.info("================================");

                        log.info("Product catalog initialized automatically.");

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
                        log.info("Customer registered: {}", customerGMXInactive.id(), customerGMXInactive.brand());

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
                        log.info("Customer registered: id:{}, brand:{}", customerGMXActive.id(),
                                        customerGMXActive.brand());

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
                        log.info("Customer registered: id:{}, brand:{}", customerWebDeInactive.id(),
                                        customerWebDeInactive.brand());

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
                        log.info("Customer registered: id:{}, brand:{}", customerWebDeActive.id(),
                                        customerWebDeActive.brand());

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
                        log.info("Customer registered: id:{}, brand:{}", customerMailInactive.id(),
                                        customerMailInactive.brand());

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

                        log.info("Customer registered: id:{}, brand:{}", customerMailActive.id(),
                                        customerMailActive.brand());
                        shop.registerCustomer(customerMailActive);

                        log.info("Customer activated");

                        shop.activateCustomer(customerGMXActive.id());
                        log.info("Customer activated: id:{}, brand:{}", customerGMXActive.id(),
                                        customerGMXActive.brand());

                        shop.activateCustomer(customerWebDeActive.id());
                        log.info("Customer activated: id:{}, brand:{}", customerWebDeActive.id(),
                                        customerWebDeActive.brand());

                        shop.activateCustomer(customerMailActive.id());
                        log.info("Customer activated: id:{}, brand:{}", customerMailActive.id(),
                                        customerMailActive.brand());

                        log.info("Customer address updated");

                        shop.updateAddress(
                                        customerGMXActive.id(),
                                        new Address(
                                                        "Main St",
                                                        "10",
                                                        "12345",
                                                        "Berlin",
                                                        "Germany"));
                        log.info("Customer address updated: id:{}, brand:{}", customerGMXActive.id(),
                                        customerGMXActive.brand());

                        shop.updateInvoiceAddress(
                                        customerGMXActive.id(),
                                        new Address(
                                                        "Bill St",
                                                        "5",
                                                        "54321",
                                                        "Munich",
                                                        "Germany"));
                        log.info("Customer invoice address updated: id:{}, brand:{}", customerGMXActive.id(),
                                        customerGMXActive.brand());

                        shop.updateCommunicationDetails(
                                        customerGMXActive.id(),
                                        new CommunicationDetails(
                                                        "gmx.active@example.com",
                                                        "+49123456789"));
                        log.info("Customer communication details updated: id:{}, brand:{}", customerGMXActive.id(),
                                        customerGMXActive.brand());

                        log.info("Product summary:");
                        printProductSummary(shop);

                        log.info("Purchase products for customer:");
                        purchaseProductsForCustomer(
                                        shop,
                                        customerGMXActive);
                        purchaseProductsForCustomer(
                                        shop,
                                        customerWebDeActive);
                        purchaseProductsForCustomer(
                                        shop,
                                        customerMailActive);

                        log.info("Contract summary:");
                        printContractSummary(
                                        shop,
                                        customerGMXActive.id());

                        log.info("Invoice:");
                        generateAndPrintInvoice(
                                        shop,
                                        customerGMXActive.id());
                        generateAndPrintInvoice(
                                        shop,
                                        customerWebDeActive.id());
                        generateAndPrintInvoice(
                                        shop,
                                        customerMailActive.id());

                        log.info("Exception scenarios:");
                        demonstrateExceptionScenarios(
                                        shop,
                                        customerGMXInactive,
                                        customerWebDeActive);

                        log.info("Utility methods:");
                        demonstrateUtilityMethods(
                                        shop,
                                        customerMailActive.id());

                        shop.deactivateCustomer(customerGMXActive.id());
                        log.info("Utility: Also removed webInactive for cleanup.");
                        shop.removeCustomer(customerWebDeActive.id());
                        log.info("Utility: Also removed webInactive for cleanup.");

                        log.info("================================");
                        log.info("MaM Shop Application Demo End");
                        log.info("================================");
                }
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
                return Customer.create(
                                firstName,
                                lastName,
                                birthDate,
                                address,
                                invoiceAddress,
                                communicationDetails,
                                brand);
        }

        private static void printProductSummary(final ShopService shop) {
                for (final Brand brand : Brand.values()) {
                        final List<Product> products = shop.loadAllProductsForBrand(brand);
                        log.info("- Brand {} : {} products.", brand, products.size());
                }
        }

        private static void purchaseProductsForCustomer(
                        final ShopService shop,
                        final Customer customer) {
                final List<Product> products = shop.loadAllProductsForBrand(customer.brand());
                log.info("Customer {} has {} products.", customer.id(), products.size());

                for (int i = 0; i < Math.min(5, products.size()); i++) {
                        try {
                                final Contract contract = shop.purchaseProduct(customer.id(), products.get(i).getId());
                                log.info("Contract {} purchased for customer {}", contract.id(), customer.id());

                                shop.activateContract(contract.id());
                                log.info("Contract {} activated for customer {}", contract.id(), customer.id());

                        } catch (Exception e) {
                                log.error("Purchase failed for customer; id: {}, error: {}", customer.id(),
                                                e.getMessage());
                        }
                }
        }

        private static void printContractSummary(
                        final ShopService shop,
                        final UUID customerId) {
                try {
                        final List<Contract> contracts = shop.loadAllContracts(customerId);
                        log.info("Customer {} has {} contracts.", customerId, contracts.size());
                } catch (Exception e) {
                        log.error("Failed to load contracts: {}", e.getMessage());
                }
        }

        private static void generateAndPrintInvoice(
                        final ShopService shop,
                        final UUID customerId) {
                try {
                        final Invoice invoice = shop.generateInvoice(customerId);
                        log.info("--- Invoice for Customer {} ({}), Brand {} ---", customerId, invoice.brand());
                        log.info("--- Total Amount: {}", invoice.totalAmount());
                        log.info("--- Items: {}", invoice.items().size());
                        log.info("-------------------------------------");
                } catch (Exception e) {
                        log.error("Failed to generate invoice: {}", e.getMessage());
                }
        }

        private static void demonstrateExceptionScenarios(
                        final ShopService shop,
                        final Customer inactive,
                        final Customer mismatchBrand) {
                System.out.println("Demonstrating exception scenarios:");

                try {
                        final List<Product> products = shop.loadAllProductsForBrand(inactive.brand());
                        log.info("Customer {} has {} products.", inactive.id(), products.size());
                        log.info("Customer {} has {} contracts.", inactive.id(),
                                        shop.loadAllContracts(inactive.id()).size());

                        shop.purchaseProduct(
                                        inactive.id(),
                                        products.get(0).getId());
                        log.info("Customer {} has {} contracts.", inactive.id(),
                                        shop.loadAllContracts(inactive.id()).size());

                } catch (Exception e) {
                        log.error("Expected (Inactive): {}", e.getMessage());
                }

                try {
                        final Brand otherBrand = mismatchBrand.brand() == Brand.GMX ? Brand.WEB_DE : Brand.GMX;
                        log.info("Customer {} has {} products.", mismatchBrand.id(),
                                        shop.loadAllProductsForBrand(otherBrand).size());

                        final List<Product> products = shop.loadAllProductsForBrand(otherBrand);
                        log.info("Customer {} has {} contracts.", mismatchBrand.id(),
                                        shop.loadAllContracts(mismatchBrand.id()).size());

                        shop.purchaseProduct(
                                        mismatchBrand.id(),
                                        products.get(0).getId());
                        log.info("Customer {} has {} contracts.", mismatchBrand.id(),
                                        shop.loadAllContracts(mismatchBrand.id()).size());
                } catch (Exception e) {
                        log.error("Expected (Brand Mismatch): {}", e.getMessage());
                }
        }

        private static void demonstrateUtilityMethods(
                        final ShopService shop,
                        final UUID customerId) {
                try {
                        final Customer loaded = shop.loadCustomer(customerId);
                        log.info("Utility: Loaded {}", loaded.firstName());

                        shop.removeCustomer(customerId);
                        log.info("Utility: Removed customer successfully.");

                        try {
                                shop.loadCustomer(customerId);
                                log.info("Utility: Confirmed customer is gone.");
                        } catch (CustomerNotFoundException e) {
                                log.info("Utility: Customer not found.");
                        }
                } catch (Exception e) {
                        log.error("Utility test failed: {}", e.getMessage());
                }
        }
}
