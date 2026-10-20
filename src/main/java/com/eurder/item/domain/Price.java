package com.eurder.item.domain;

import com.eurder.shared.InvalidInputException;

import java.math.BigDecimal;

import static com.eurder.shared.Validation.requireValue;

public record Price(BigDecimal amount) {

    private static final int DECIMALS = 2;

    public Price {
        requireValue(amount, "Price");
        if (amount.signum() < 0) {
            throw new InvalidInputException("Price must not be negative");
        }
        if (amount.stripTrailingZeros().scale() > DECIMALS) {
            throw new InvalidInputException("Price must not have more than " + DECIMALS + " decimals");
        }
        amount = amount.setScale(DECIMALS);
    }
}