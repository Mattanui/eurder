package com.eurder.customer.domain;

import jakarta.persistence.Embeddable;

import static com.eurder.shared.Validation.requireText;

@Embeddable
public record Address(String street, String houseNumber, String postalCode, String city, String country) {

    public Address {
        street = requireText(street, "Street");
        houseNumber = requireText(houseNumber, "House number");
        postalCode = requireText(postalCode, "Postal code");
        city = requireText(city, "City");
        country = requireText(country, "Country");
    }
}