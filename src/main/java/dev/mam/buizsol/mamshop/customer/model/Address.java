package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotBlank;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateCustomerModel;

public record Address(
        @NotBlank String street,
        @NotBlank String number,
        @NotBlank String postcode,
        @NotBlank String city,
        @NotBlank String country) {

    public Address {
        validateCustomerModel(street, "Street");
        validateCustomerModel(number, "Number");
        validateCustomerModel(postcode, "Postcode");
        validateCustomerModel(city, "City");
        validateCustomerModel(country, "Country");
    }


}
