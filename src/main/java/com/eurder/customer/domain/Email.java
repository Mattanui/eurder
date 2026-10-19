package com.eurder.customer.domain;

import com.eurder.shared.InvalidInputException;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException("Email must be provided");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_FORMAT.matcher(value).matches()) {
            throw new InvalidInputException("Email has an invalid format: " + value);
        }
    }
}