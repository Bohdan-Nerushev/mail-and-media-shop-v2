//
// package dev.mam.buizsol.mamshop.billing.model;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.UUID;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.CsvSource;
//
// @DisplayName("InvoiceItem Tests")
// class InvoiceItemTest {
//
//    @DisplayName("Successful creation of InvoiceItem with valid data")
//    @Test
//    void shouldCreateInvoiceItemWhenValidDataProvided() {
//        UUID productId = UUID.randomUUID();
//        String productName = "Test Product";
//        UUID contractId = UUID.randomUUID();
//        LocalDate creationDate = LocalDate.now();
//        BigDecimal setupFee = new BigDecimal("10.00");
//        BigDecimal monthlyFee = new BigDecimal("5.00");
//
//        InvoiceItem item = new InvoiceItem(productId, productName, contractId, creationDate, setupFee, monthlyFee);
//
//        assertEquals(productId, item.productId());
//        assertEquals(productName, item.productName());
//        assertEquals(contractId, item.contractId());
//        assertEquals(creationDate, item.contractCreationDate());
//        assertEquals(setupFee, item.setupFee());
//        assertEquals(monthlyFee, item.monthlyFee());
//    }
//
//    @ParameterizedTest(name = "Check of fees - setup: {0}, monthly: {1}")
//    @DisplayName("Verification of setup and monthly fees using various values")
//    @CsvSource({ "0.00, 0.11", "4.99, 9.99", "99.99, 149.50", "0.01, 0.11" })
//    void shouldSetCorrectFeesWhenValuesAreProvided(BigDecimal setupFee, BigDecimal monthlyFee) {
//        InvoiceItem item = new InvoiceItem(UUID.randomUUID(), "Product", UUID.randomUUID(), LocalDate.now(), setupFee,
//                monthlyFee);
//
//        assertEquals(setupFee, item.setupFee());
//        assertEquals(monthlyFee, item.monthlyFee());
//    }
//
//    @Test
//    @DisplayName("Verification of InvoiceItem with boundary date (yesterday)")
//    void shouldHandlePastDateWhenCreatingInvoiceItem() {
//        LocalDate pastDate = LocalDate.now().minusYears(1);
//
//        InvoiceItem item = new InvoiceItem(
//                UUID.randomUUID(),
//                "Old Contract",
//                UUID.randomUUID(),
//                pastDate,
//                BigDecimal.ZERO,
//                new BigDecimal("10.00"));
//
//        assertEquals(pastDate, item.contractCreationDate());
//    }
// }
