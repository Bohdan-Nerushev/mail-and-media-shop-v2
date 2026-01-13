package com.unitedinternet.buizsol.mamshop.customer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void shouldCreateAddressInstanceWhenDataIsValid() {
        Address address = new Address("Main St", "10", "12345", "Berlin", "Germany");
        Assertions.assertEquals("Main St", address.getStreet());
        Assertions.assertEquals("10", address.getNumber());
        Assertions.assertEquals("12345", address.getPostcode());
        Assertions.assertEquals("Berlin", address.getCity());
        Assertions.assertEquals("Germany", address.getCountry());
    }

    @Test
    void shouldThrowExceptionWhenStreetIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Address("", "10", "12345", "Berlin", "Germany"));
    }

    @Test
    void shouldThrowExceptionWhenNumberIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Address("Main St", null, "12345", "Berlin", "Germany"));
    }
}
