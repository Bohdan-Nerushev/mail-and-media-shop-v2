package dev.mam.buizsol.mamshop.billing.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceItemTest {

    @Test
    @DisplayName("01: Successful creation of InvoiceItem with valid data")
    void test1_invoiceItemCreation_success() {
        UUID productId = UUID.randomUUID();
        String productName = "Test Product";
        UUID contractId = UUID.randomUUID();
        LocalDate creationDate = LocalDate.now();
        BigDecimal setupFee = new BigDecimal("10.00");
        BigDecimal monthlyFee = new BigDecimal("5.00");

        InvoiceItem item = new InvoiceItem(productId, productName, contractId, creationDate, setupFee, monthlyFee);

        assertEquals(productId, item.productId());
        assertEquals(productName, item.productName());
        assertEquals(contractId, item.contractId());
        assertEquals(creationDate, item.contractCreationDate());
        assertEquals(setupFee, item.setupFee());
        assertEquals(monthlyFee, item.monthlyFee());
    }

    @ParameterizedTest(name = "02: Parameterized check of fees - setup: {0}, monthly: {1}")
    @DisplayName("02: Verification of setup and monthly fees using various values")
    @CsvSource({
            "0.00, 0.11",
            "4.99, 9.99",
            "99.99, 149.50",
            "0.01, 0.11"
    })
    void test2_invoiceItemFees_parameterized(BigDecimal setupFee, BigDecimal monthlyFee) {
        InvoiceItem item = new InvoiceItem(
                UUID.randomUUID(),
                "Product",
                UUID.randomUUID(),
                LocalDate.now(),
                setupFee,
                monthlyFee);

        assertEquals(setupFee, item.setupFee());
        assertEquals(monthlyFee, item.monthlyFee());
    }

    @Test
    @DisplayName("03: Verification of InvoiceItem with boundary date (yesterday)")
    void test3_invoiceItemWithPastDate_success() {
        LocalDate pastDate = LocalDate.now().minusYears(1);

        InvoiceItem item = new InvoiceItem(
                UUID.randomUUID(),
                "Old Contract",
                UUID.randomUUID(),
                pastDate,
                BigDecimal.ZERO,
                new BigDecimal("10.00"));

        assertEquals(pastDate, item.contractCreationDate());
    }
}
