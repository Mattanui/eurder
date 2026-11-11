package com.eurder.customer.domain;

import jakarta.persistence.Embeddable;

import static com.eurder.shared.Validation.requireText;

@Embeddable
public record PhoneNumber(String countryCode, String number) {

    public PhoneNumber {
        countryCode = requireText(countryCode, "Phone country code");
        number = requireText(number, "Phone number");
    }
}