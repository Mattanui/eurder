package com.eurder.customer.domain;

import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneNumberTest {

    @Test
    void createPhoneNumber_withValidValues_keepsThem() {
        PhoneNumber phoneNumber = new PhoneNumber("+32", "485652311");

        assertThat(phoneNumber)
                .extracting(PhoneNumber::countryCode, PhoneNumber::number)
                .containsExactly("+32", "485652311");
    }

    @Test
    void createPhoneNumber_withSurroundingSpaces_isTrimmed() {
        PhoneNumber phoneNumber = new PhoneNumber("  +32 ", " 485652311  ");

        assertThat(phoneNumber)
                .extracting(PhoneNumber::countryCode, PhoneNumber::number)
                .containsExactly("+32", "485652311");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createPhoneNumber_withoutCountryCode_isRejected(String missingValue) {
        assertThatThrownBy(() -> new PhoneNumber(missingValue, "485652311"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Phone country code must be provided");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createPhoneNumber_withoutNumber_isRejected(String missingValue) {
        assertThatThrownBy(() -> new PhoneNumber("+32", missingValue))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Phone number must be provided");
    }
}