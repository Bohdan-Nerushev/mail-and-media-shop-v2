package dev.mam.buizsol.mamshop.customer.model;

import dev.mam.buizsol.mamshop.customer.exception.CustomerValidationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CommunicationDetails Tests")
class CommunicationDetailsTest {

    private CommunicationDetails createDefaultCommunicationDetails(
            String email,
            String telephone) {
        return new CommunicationDetails(
                email,
                telephone);
    }

    @Test
    @DisplayName("Positive: Successful creation with valid data")
    void shouldCreateCommunicationDetailsWhenDataIsValid() {
        CommunicationDetails details = createDefaultCommunicationDetails("test@example.com", "+123456789");
        assertEquals("test@example.com", details.email());
        assertEquals("+123456789", details.telephone());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("Negative: Validation of 'email' field")
    void shouldThrowExceptionWhenEmailIsInvalid(String invalidEmail) {
        assertThrows(CustomerValidationException.class,
                () -> createDefaultCommunicationDetails(invalidEmail, "+123456789"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("Negative: Validation of 'telephone' field")
    void shouldThrowExceptionWhenTelephoneIsInvalid(String invalidPhone) {
        assertThrows(CustomerValidationException.class,
                () -> createDefaultCommunicationDetails("test@example.com", invalidPhone));
    }

    @Test
    @DisplayName("Negative: Validation of 'telephone' field, String to Long ")
    void shouldThrowExceptionWhenTelephoneIsTooLong() {
        String longTelephone = "1".repeat(250);
        assertThrows(CustomerValidationException.class,
                () -> createDefaultCommunicationDetails("test@example.com", longTelephone));
    }

    @Test
    @DisplayName("Negative: Validation of 'email' field, String to small ")
    void shouldThrowExceptionWhenEmailIsTooSmall() {
        String longEmail = "";
        assertThrows(CustomerValidationException.class,
                () -> createDefaultCommunicationDetails(longEmail, "123"));
    }

    @Test
    @DisplayName("Negative: Validation of 'telephone' field, String to small ")
    void shouldThrowExceptionWhenTelephoneIsTooSmall() {
        String longTelephone = "";
        assertThrows(CustomerValidationException.class,
                () -> createDefaultCommunicationDetails("test@example.com", longTelephone));
    }

    @Test
    @DisplayName("Boundary: Shortest valid phone number")
    void shouldHandleShortTelephone() {
        CommunicationDetails details = createDefaultCommunicationDetails("a@b.c", "0");
        assertEquals("0", details.telephone());
    }

    @ParameterizedTest
    @ValueSource(strings = { "user@domain.com", "user.name@sub.domain.org", "123@456.789" })
    @DisplayName("Positive: Support for various email formats")
    void shouldHandleVariousEmailFormats(String email) {
        CommunicationDetails details = createDefaultCommunicationDetails(email, "12345");
        assertEquals(email, details.email());
    }

    @ParameterizedTest
    @ValueSource(strings = { "+4912345678", "004912345678", "123", "0151-1234567" })
    @DisplayName("Positive: Support for various telephone formats")
    void shouldHandleVariousTelephoneFormats(String phone) {
        CommunicationDetails details = createDefaultCommunicationDetails("test@test.com", phone);
        assertEquals(phone, details.telephone());
    }
}
