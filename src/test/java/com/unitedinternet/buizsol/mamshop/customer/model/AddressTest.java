package com.unitedinternet.buizsol.mamshop.customer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AddressTest {

    @Test
    @DisplayName("1. Positive: Successful creation with valid data")
    void shouldCreateAddressInstanceWhenDataIsValid() {
        Address address = new Address("Main St", "10", "12345", "Berlin", "Germany");
        Assertions.assertEquals("Main St", address.street());
        Assertions.assertEquals("10", address.number());
        Assertions.assertEquals("12345", address.postcode());
        Assertions.assertEquals("Berlin", address.city());
        Assertions.assertEquals("Germany", address.country());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("2. Negative: Validation of 'street' field")
    void shouldThrowExceptionWhenStreetIsInvalid(String invalidStreet) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Address(invalidStreet, "10", "12345", "Berlin", "Germany"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("3. Negative: Validation of 'number' field")
    void shouldThrowExceptionWhenNumberIsInvalid(String invalidNumber) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Address("Main St", invalidNumber, "12345", "Berlin", "Germany"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("4. Negative: Validation of 'postcode' field")
    void shouldThrowExceptionWhenPostcodeIsInvalid(String invalidPostcode) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Address("Main St", "10", invalidPostcode, "Berlin", "Germany"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("5. Negative: Validation of 'city' field")
    void shouldThrowExceptionWhenCityIsInvalid(String invalidCity) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Address("Main St", "10", "12345", invalidCity, "Germany"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    @DisplayName("6. Negative: Validation of 'country' field")
    void shouldThrowExceptionWhenCountryIsInvalid(String invalidCountry) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Address("Main St", "10", "12345", "Berlin", invalidCountry));
    }

    @Test
    @DisplayName("7. Boundary: Extremely long string for street")
    void shouldHandleExtremelyLongStreet() {
        String longStreet = "A".repeat(1000);
        Address address = new Address(longStreet, "1", "12345", "Berlin", "Germany");
        Assertions.assertEquals(longStreet, address.street());
    }

    @Test
    @DisplayName("8. Boundary: Minimal valid input (single characters)")
    void shouldHandleSingleCharacterInputs() {
        Address address = new Address("S", "1", "1", "B", "G");
        Assertions.assertEquals("S", address.street());
        Assertions.assertEquals("1", address.number());
        Assertions.assertEquals("1", address.postcode());
        Assertions.assertEquals("B", address.city());
        Assertions.assertEquals("G", address.country());
    }

    @Test
    @DisplayName("9. Positive: Equals and HashCode consistency")
    void shouldBeEqualWhenContentIsSame() {
        Address address1 = new Address("St", "1", "123", "City", "Country");
        Address address2 = new Address("St", "1", "123", "City", "Country");

        Assertions.assertEquals(address1, address2);
        Assertions.assertEquals(address1.hashCode(), address2.hashCode());
    }

    @Test
    @DisplayName("10. Positive: Inequality check")
    void shouldNotBeEqualWhenContentIsDifferent() {
        Address address1 = new Address("St1", "1", "123", "City", "Country");
        Address address2 = new Address("St2", "1", "123", "City", "Country");

        Assertions.assertNotEquals(address1, address2);
    }
}
