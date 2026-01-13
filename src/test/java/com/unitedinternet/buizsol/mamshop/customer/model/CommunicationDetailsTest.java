package com.unitedinternet.buizsol.mamshop.customer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CommunicationDetailsTest {

    @Test
    void shouldCreateCommunicationDetailsWhenDataIsValid() {
        CommunicationDetails details = new CommunicationDetails("test@example.com", "+123456789");
        Assertions.assertEquals("test@example.com", details.getEmail());
        Assertions.assertEquals("+123456789", details.getTelephone());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CommunicationDetails(" ", "+123456789"));
    }

    @Test
    void shouldThrowExceptionWhenTelephoneIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CommunicationDetails("test@example.com", null));
    }
}
