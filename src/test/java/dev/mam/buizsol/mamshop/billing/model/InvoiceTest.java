//package dev.mam.buizsol.mamshop.billing.model;
//
//import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
//import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidationException;
//import dev.mam.buizsol.mamshop.customer.model.Address;
//import dev.mam.buizsol.mamshop.customer.model.Brand;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("Invoice Record Tests")
//class InvoiceTest {
//
//    private Address testAddress;
//    private final UUID customerId = UUID.randomUUID();
//
//    @BeforeEach
//    void setUp() {
//        testAddress = new Address("Street", "1", "12345", "City", "Country");
//    }
//
//    @Test
//    @DisplayName("Successful calculation of totals for multiple items")
//    void shouldCalculateTotalsCorrectlyWhenMultipleItemsExist() {
//        InvoiceItem item1 = new InvoiceItem(UUID.randomUUID(), "P1", UUID.randomUUID(), LocalDate.now(),
//                new BigDecimal("10.00"), new BigDecimal("5.00"));
//        InvoiceItem item2 = new InvoiceItem(UUID.randomUUID(), "P2", UUID.randomUUID(), LocalDate.now(),
//                new BigDecimal("20.00"), new BigDecimal("15.00"));
//
//        BigDecimal discount = new BigDecimal("7.00");
//
//        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, List.of(item1, item2), discount);
//
//        assertEquals(new BigDecimal("30.00"), invoice.totalSetupFee());
//        assertEquals(new BigDecimal("20.00"), invoice.totalMonthlyFee());
//        assertEquals(new BigDecimal("43.00"), invoice.totalAmount());
//        assertEquals(LocalDate.now(), invoice.invoiceDate());
//        assertEquals(Brand.GMX, invoice.brand());
//    }
//
//    @Test
//    @DisplayName("Successful creation with zero discount (boundary value)")
//    void shouldAllowCreationWhenDiscountIsZero() {
//        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, Collections.emptyList(),
//                BigDecimal.ZERO);
//
//        assertEquals(BigDecimal.ZERO, invoice.discount());
//        assertEquals(BigDecimal.ZERO, invoice.totalAmount());
//    }
//
//    @DisplayName("Verification that invalid discounts (negative/null) throw exception")
//    @ParameterizedTest(name = "Invalid discount validation - value: {0}")
//    @ValueSource(strings = { "-0.01", "-10.00" })
//    void shouldThrowExceptionWhenDiscountIsNegative(BigDecimal negativeDiscount) {
//        assertThrows(InvalidInvoiceDiscountException.class, () -> new Invoice(Brand.GMX, customerId, testAddress,
//                testAddress, Collections.emptyList(), negativeDiscount));
//    }
//
//    @Test
//    @DisplayName("Null discount validation (negative scenario)")
//    void shouldThrowExceptionWhenDiscountIsNull() {
//        assertThrows(InvalidInvoiceDiscountException.class,
//                () -> new Invoice(Brand.GMX, customerId, testAddress, testAddress, Collections.emptyList(), null));
//    }
//
//    @DisplayName("Verification of totals calculation using parameterized discounts")
//    @ParameterizedTest(name = "Calculation logic with various item counts and discount: {0}")
//    @CsvSource({
//            "0.00, 0.00",
//            "10.50, -10.50",
//            "100.00, -100.00"
//    })
//    void shouldCalculateAmountCorrectlyWhenDiscountsAreProvided(BigDecimal discount, BigDecimal expectedTotal) {
//
//        Invoice invoice = new Invoice(Brand.MAIL_COM, customerId, testAddress, testAddress, Collections.emptyList(),
//                discount);
//
//        assertEquals(expectedTotal, invoice.totalAmount());
//    }
//
//    @Test
//    @DisplayName("Verification of items list immutability")
//    void shouldReturnImmutableItemsList() {
//
//        InvoiceItem item = new InvoiceItem(UUID.randomUUID(), "P1", UUID.randomUUID(), LocalDate.now(),
//                BigDecimal.ZERO, BigDecimal.TEN);
//        List<InvoiceItem> items = new java.util.ArrayList<>();
//        items.add(item);
//
//        Invoice invoice = new Invoice(Brand.WEB_DE, customerId, testAddress, testAddress, items, BigDecimal.ZERO);
//
//        assertThrows(UnsupportedOperationException.class, () -> invoice.items().add(item));
//    }
//
//    @Test
//    @DisplayName("Verification of different mailing and invoice addresses")
//    void shouldStoreDifferentMailingAndInvoiceAddresses() {
//        Address invoiceAddress = new Address("Invoice St", "2", "54321", "City2", "Country");
//
//        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, invoiceAddress, Collections.emptyList(),
//                BigDecimal.ZERO);
//
//        assertEquals(testAddress, invoice.address());
//        assertEquals(invoiceAddress, invoice.invoiceAddress());
//    }
//
//    @Test
//    @DisplayName("Null items list validation (negative scenario)")
//    void shouldThrowExceptionWhenItemsListIsNull() {
//        assertThrows(InvoiceValidationException.class,
//                () -> new Invoice(Brand.GMX, customerId, testAddress, testAddress, null, BigDecimal.ZERO));
//    }
//
//    @Test
//    @DisplayName("Precision verification with many fractional fee items")
//    void shouldMaintainPrecisionWhenCalculatingTotals() {
//        BigDecimal fee = new BigDecimal("1.3333333333"); // Extreme precision
//        InvoiceItem item1 = new InvoiceItem(UUID.randomUUID(), "P1", UUID.randomUUID(), LocalDate.now(), fee, fee);
//        InvoiceItem item2 = new InvoiceItem(UUID.randomUUID(), "P2", UUID.randomUUID(), LocalDate.now(), fee, fee);
//
//        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, List.of(item1, item2),
//                BigDecimal.ZERO);
//
//        BigDecimal expectedSetup = fee.add(fee);
//        BigDecimal expectedMonthly = fee.add(fee);
//        assertEquals(expectedSetup, invoice.totalSetupFee());
//        assertEquals(expectedMonthly, invoice.totalMonthlyFee());
//        assertEquals(expectedSetup.add(expectedMonthly), invoice.totalAmount());
//    }
//
//    @Test
//    @DisplayName("Discount exceeding total fees (Negative total amount scenario)")
//    void shouldAllowNegativeTotalAmountWhenDiscountExceedsFees() {
//        InvoiceItem item = new InvoiceItem(UUID.randomUUID(), "P1", UUID.randomUUID(), LocalDate.now(),
//                new BigDecimal("10.00"), new BigDecimal("10.00"));
//        BigDecimal highDiscount = new BigDecimal("25.00");
//
//        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, List.of(item), highDiscount);
//
//        assertEquals(new BigDecimal("-5.00"), invoice.totalAmount());
//    }
//
//    @Test
//    @DisplayName("Stress test with a large number of items (performance check)")
//    void shouldCalculateTotalsCorrectlyWhenLargeNumberOfItemsProvided() {
//        final InvoiceItem prototype = new InvoiceItem(
//                UUID.randomUUID(), "Product", UUID.randomUUID(), LocalDate.now(),
//                new BigDecimal("1.00"), new BigDecimal("2.00"));
//
//        final List<InvoiceItem> items = Collections.nCopies(1000, prototype);
//
//        final Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, items, BigDecimal.ZERO);
//
//        assertEquals(new BigDecimal("1000.00"), invoice.totalSetupFee());
//        assertEquals(new BigDecimal("2000.00"), invoice.totalMonthlyFee());
//        assertEquals(new BigDecimal("3000.00"), invoice.totalAmount());
//    }
//}
