package com.eurder.shared;

public final class Validation {

    private Validation() {
        // utility class: holds static helpers only, never instantiated
    }

    public static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw missing(fieldName);
        }
        return value.trim();
    }

    public static <T> T requireValue(T value, String fieldName) {
        if (value == null) {
            throw missing(fieldName);
        }
        return value;
    }

    private static InvalidInputException missing(String fieldName) {
        return new InvalidInputException(fieldName + " must be provided");
    }
}