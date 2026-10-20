package com.eurder.shared;

public final class Validation {

    private Validation() {
        // utility class: holds static helpers only, never instantiated
    }

    public static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException(fieldName + " must be provided");
        }
        return value.trim();
    }
}