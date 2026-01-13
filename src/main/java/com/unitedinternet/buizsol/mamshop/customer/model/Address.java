package com.unitedinternet.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class Address {

    private final String street;
    private final String number;
    private final String postcode;
    private final String city;
    private final String country;


    public Address(
            @NotBlank(message = "Street must not be blank") String street,
            @NotBlank(message = "Number must not be blank") String number,
            @NotBlank(message = "Postcode must not be blank") String postcode,
            @NotBlank(message = "City must not be blank") String city,
            @NotBlank(message = "Country must not be blank") String country) {
        validate(street, "Street");
        validate(number, "Number");
        validate(postcode, "Postcode");
        validate(city, "City");
        validate(country, "Country");

        this.street = street;
        this.number = number;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
    }

    @NotNull
    public String getStreet() {
        return street;
    }

    @NotNull
    public String getNumber() {
        return number;
    }

    @NotNull
    public String getPostcode() {
        return postcode;
    }

    @NotNull
    public String getCity() {
        return city;
    }

    @NotNull
    public String getCountry() {
        return country;
    }

    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }
}
