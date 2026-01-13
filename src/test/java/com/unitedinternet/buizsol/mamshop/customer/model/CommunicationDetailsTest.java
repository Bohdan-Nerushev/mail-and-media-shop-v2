package com.unitedinternet.buizsol.mamshop.customer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CommunicationDetailsTest {

    @Test
    @DisplayName("1. Positive: Successful creation with valid data")
    void shouldCreateCommunicationDetailsWhenDataIsValid() {
        CommunicationDetails details = new CommunicationDetails("test@example.com", "+123456789");
        Assertions.assertEquals("test@example.com", details.getEmail());
        Assertions.assertEquals("+123456789", details.getTelephone());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("2. Negative: Validation of 'email' field")
    void shouldThrowExceptionWhenEmailIsInvalid(String invalidEmail) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CommunicationDetails(invalidEmail, "+123456789"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("3. Negative: Validation of 'telephone' field")
    void shouldThrowExceptionWhenTelephoneIsInvalid(String invalidPhone) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CommunicationDetails("test@example.com", invalidPhone));
    }

    @Test
    @DisplayName("4. Boundary: Extremely long email string")
    void shouldHandleExtremelyLongEmail() {
        String longEmail = "a".repeat(250) + "@example.com";
        CommunicationDetails details = new CommunicationDetails(longEmail, "123");
        Assertions.assertEquals(longEmail, details.getEmail());
    }

    @Test
    @DisplayName("5. Boundary: Shortest valid phone number")
    void shouldHandleShortTelephone() {
        CommunicationDetails details = new CommunicationDetails("a@b.c", "0");
        Assertions.assertEquals("0", details.getTelephone());
    }

    @ParameterizedTest
    @ValueSource(strings = { "user@domain.com", "user.name@sub.domain.org", "123@456.789" })
    @DisplayName("6. Positive: Support for various email formats")
    void shouldHandleVariousEmailFormats(String email) {
        CommunicationDetails details = new CommunicationDetails(email, "12345");
        Assertions.assertEquals(email, details.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = { "+4912345678", "004912345678", "123", "0151-1234567" })
    @DisplayName("7. Positive: Support for various telephone formats")
    void shouldHandleVariousTelephoneFormats(String phone) {
        CommunicationDetails details = new CommunicationDetails("test@test.com", phone);
        Assertions.assertEquals(phone, details.getTelephone());
    }
}
