package com.eurder.item.domain;

import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Test
    void createPrice_withTwoDecimals_keepsTheAmount() {
        Price price = new Price(new BigDecimal("699.95"));

        assertThat(price.amount()).isEqualTo(new BigDecimal("699.95"));
    }

    @ParameterizedTest
    @CsvSource({
            "10,     10.00",
            "10.5,   10.50",
            "12.340, 12.34"
    })
    void createPrice_isNormalizedToTwoDecimals(String input, String expected) {
        Price price = new Price(new BigDecimal(input));

        assertThat(price.amount()).isEqualTo(new BigDecimal(expected));
    }

    @Test
    void twoPrices_withSameValueButDifferentScale_areEqual() {
        assertThat(new Price(new BigDecimal("10")))
                .isEqualTo(new Price(new BigDecimal("10.00")));
    }

    @Test
    void createPrice_withZero_isAccepted() {
        Price price = new Price(BigDecimal.ZERO);

        assertThat(price.amount()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void createPrice_withoutAmount_isRejected() {
        assertThatThrownBy(() -> new Price(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Price must be provided");
    }

    @Test
    void createPrice_withNegativeAmount_isRejected() {
        assertThatThrownBy(() -> new Price(new BigDecimal("-0.01")))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Price must not be negative");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12.345", "0.001"})
    void createPrice_withMoreThanTwoDecimals_isRejected(String amount) {
        assertThatThrownBy(() -> new Price(new BigDecimal(amount)))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Price must not have more than 2 decimals");
    }
}