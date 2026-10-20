package com.eurder.shared;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.eurder.shared.Validation.requireText;

public record Email(String value) {

    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        value = requireText(value, "Email").toLowerCase(Locale.ROOT);
        if (!EMAIL_FORMAT.matcher(value).matches()) {
            throw new InvalidInputException("Email has an invalid format: " + value);
        }
    }
}