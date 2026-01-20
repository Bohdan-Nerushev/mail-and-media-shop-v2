package dev.mam.buizsol.mamshop.billing.model;

import dev.mam.buizsol.mamshop.billing.exception.InvalidInvoiceDiscountException;
import dev.mam.buizsol.mamshop.billing.exception.InvoiceValidateException;
import dev.mam.buizsol.mamshop.customer.model.Address;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    private Address testAddress;
    private final UUID customerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testAddress = new Address("Street", "1", "12345", "City", "Country");
    }

    @Test
    @DisplayName("01: Successful calculation of totals for multiple items")
    void test1_invoiceTotalsCalculation_success() {
        InvoiceItem item1 = new InvoiceItem(UUID.randomUUID(), "P1", UUID.randomUUID(), LocalDate.now(),
                new BigDecimal("10.00"), new BigDecimal("5.00"));
        InvoiceItem item2 = new InvoiceItem(UUID.randomUUID(), "P2", UUID.randomUUID(), LocalDate.now(),
                new BigDecimal("20.00"), new BigDecimal("15.00"));

        BigDecimal discount = new BigDecimal("7.00");

        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, List.of(item1, item2), discount);

        assertEquals(new BigDecimal("30.00"), invoice.getTotalSetupFee());
        assertEquals(new BigDecimal("20.00"), invoice.getTotalMonthlyFee());
        assertEquals(new BigDecimal("43.00"), invoice.getTotalAmount());
        assertEquals(LocalDate.now(), invoice.getInvoiceDate());
        assertEquals(Brand.GMX, invoice.getBrand());
    }

    @Test
    @DisplayName("02: Successful creation with zero discount (boundary value)")
    void test2_invoiceWithZeroDiscount_success() {
        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, Collections.emptyList(),
                BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, invoice.getDiscount());
        assertEquals(BigDecimal.ZERO, invoice.getTotalAmount());
    }

    @ParameterizedTest(name = "Test 3: Invalid discount validation - value: {0}")
    @DisplayName("03: Verification that invalid discounts (negative/null) throw exception")
    @ValueSource(strings = { "-0.01", "-10.00" })
    void test3_invoiceNegativeDiscount_throwsException(BigDecimal negativeDiscount) {
        assertThrows(InvalidInvoiceDiscountException.class, () -> new Invoice(Brand.GMX, customerId, testAddress,
                testAddress, Collections.emptyList(), negativeDiscount));
    }

    @Test
    @DisplayName("04: Null discount validation (negative scenario)")
    void test4_invoiceNullDiscount_throwsException() {
        assertThrows(InvalidInvoiceDiscountException.class,
                () -> new Invoice(Brand.GMX, customerId, testAddress, testAddress, Collections.emptyList(), null));
    }

    @ParameterizedTest(name = "Test 5: Calculation logic with various item counts and discount: {0}")
    @DisplayName("05: Verification of totals calculation using parameterized discounts")
    @CsvSource({
            "0.00, 0.00",
            "10.50, -10.50",
            "100.00, -100.00"
    })
    void test5_invoiceCalculations_parameterized(BigDecimal discount, BigDecimal expectedTotal) {

        Invoice invoice = new Invoice(Brand.MAIL_COM, customerId, testAddress, testAddress, Collections.emptyList(),
                discount);

        assertEquals(expectedTotal, invoice.getTotalAmount());
    }

    @Test
    @DisplayName("06: Verification of items list immutability")
    void test6_invoiceItemsList_isImmutable() {

        InvoiceItem item = new InvoiceItem(UUID.randomUUID(), "P1", UUID.randomUUID(), LocalDate.now(),
                BigDecimal.ZERO, BigDecimal.TEN);
        List<InvoiceItem> items = new java.util.ArrayList<>();
        items.add(item);

        Invoice invoice = new Invoice(Brand.WEB_DE, customerId, testAddress, testAddress, items, BigDecimal.ZERO);

        assertThrows(UnsupportedOperationException.class, () -> invoice.getItems().add(item));
    }

    @Test
    @DisplayName("07: Verification of different mailing and invoice addresses")
    void test7_invoiceDifferentAddresses_success() {
        Address invoiceAddress = new Address("Invoice St", "2", "54321", "City2", "Country");

        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, invoiceAddress, Collections.emptyList(),
                BigDecimal.ZERO);

        assertEquals(testAddress, invoice.getAddress());
        assertEquals(invoiceAddress, invoice.getInvoiceAddress());
    }

    @Test
    @DisplayName("08: Null items list validation (negative scenario)")
    void test8_invoiceNullItems_throwsException() {
        assertThrows(InvoiceValidateException.class,
                () -> new Invoice(Brand.GMX, customerId, testAddress, testAddress, null, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("09: Precision verification with many fractional fee items")
    void test9_invoicePrecision_correctSum() {
        BigDecimal fee = new BigDecimal("1.3333333333"); // Extreme precision
        InvoiceItem item1 = new InvoiceItem(UUID.randomUUID(), "P1", UUID.randomUUID(), LocalDate.now(), fee, fee);
        InvoiceItem item2 = new InvoiceItem(UUID.randomUUID(), "P2", UUID.randomUUID(), LocalDate.now(), fee, fee);

        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, List.of(item1, item2),
                BigDecimal.ZERO);

        BigDecimal expectedSetup = fee.add(fee);
        BigDecimal expectedMonthly = fee.add(fee);
        assertEquals(expectedSetup, invoice.getTotalSetupFee());
        assertEquals(expectedMonthly, invoice.getTotalMonthlyFee());
        assertEquals(expectedSetup.add(expectedMonthly), invoice.getTotalAmount());
    }

    @Test
    @DisplayName("10: Discount exceeding total fees (Negative total amount scenario)")
    void test10_invoiceDiscountExceedingTotals_resultsInNegativeAmount() {
        InvoiceItem item = new InvoiceItem(UUID.randomUUID(), "P1", UUID.randomUUID(), LocalDate.now(),
                new BigDecimal("10.00"), new BigDecimal("10.00"));
        BigDecimal highDiscount = new BigDecimal("25.00");

        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, List.of(item), highDiscount);

        assertEquals(new BigDecimal("-5.00"), invoice.getTotalAmount());
    }

    @Test
    @DisplayName("11: Stress test with a large number of items (performance check)")
    void test11_invoiceLargeItemsList_calculatesCorrectly() {
        int count = 1000;
        java.util.List<InvoiceItem> items = new java.util.ArrayList<>(count);
        BigDecimal setup = new BigDecimal("1.00");
        BigDecimal monthly = new BigDecimal("2.00");

        for (int i = 0; i < count; i++) {
            items.add(new InvoiceItem(UUID.randomUUID(), "P" + i, UUID.randomUUID(), LocalDate.now(), setup, monthly));
        }

        Invoice invoice = new Invoice(Brand.GMX, customerId, testAddress, testAddress, items, BigDecimal.ZERO);

        assertEquals(new BigDecimal("1000.00"), invoice.getTotalSetupFee());
        assertEquals(new BigDecimal("2000.00"), invoice.getTotalMonthlyFee());
        assertEquals(new BigDecimal("3000.00"), invoice.getTotalAmount());
    }
}
