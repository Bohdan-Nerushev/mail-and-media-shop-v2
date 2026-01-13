package com.unitedinternet.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class Address {

    @NotNull
    private final String street;
    @NotNull
    private final String number;
    @NotNull
    private final String postcode;
    @NotNull
    private final String city;
    @NotNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;

        if (!street.equals(address.street)) {
            return false;
        }
        if (!number.equals(address.number)) {
            return false;
        }
        if (!postcode.equals(address.postcode)) {
            return false;
        }
        if (!city.equals(address.city)) {
            return false;
        }
        return country.equals(address.country);
    }

    @Override
    public int hashCode() {
        int result = street.hashCode();
        result = 31 * result + number.hashCode();
        result = 31 * result + postcode.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + country.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", postcode='" + postcode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

}
