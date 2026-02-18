package dev.mam.buizsol.mamshop.customer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static dev.mam.buizsol.mamshop.config.ValidationUtils.validateCustomerModel;

public record Address(
        @NotBlank @Size(min = 1, max = 200) String street,
        @NotBlank @Size(min = 1, max = 100) String number,
        @NotBlank @Size(min = 1, max = 100) String postcode,
        @NotBlank @Size(min = 1, max = 100) String city,
        @NotBlank @Size(min = 1, max = 100) String country) {

    public Address {
        validateCustomerModel(street, "Street", 200);
        validateCustomerModel(number, "Number");
        validateCustomerModel(postcode, "Postcode");
        validateCustomerModel(city, "City");
        validateCustomerModel(country, "Country");
    }

}
