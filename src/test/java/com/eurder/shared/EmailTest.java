package com.eurder.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void createEmail_withValidAddress_keepsTheValue() {
        Email email = new Email("jimmy.hendrix@dhemail.com");

        assertThat(email.value()).isEqualTo("jimmy.hendrix@dhemail.com");
    }

    @Test
    void createEmail_withUppercaseAndSurroundingSpaces_isNormalized() {
        Email email = new Email("  Jimmy.Hendrix@DHEmail.com ");

        assertThat(email.value()).isEqualTo("jimmy.hendrix@dhemail.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createEmail_withoutValue_isRejected(String missingValue) {
        assertThatThrownBy(() -> new Email(missingValue))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Email must be provided");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "jimmy",
            "jimmy@",
            "@dhemail.com",
            "jimmy@dhemail",
            "jimmy hendrix@dhemail.com",
            "jimmy@@dhemail.com"
    })
    void createEmail_withInvalidFormat_isRejected(String invalidValue) {
        assertThatThrownBy(() -> new Email(invalidValue))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Email has an invalid format: " + invalidValue);
    }

    @Test
    void twoEmails_withSameNormalizedValue_areEqual() {
        assertThat(new Email("jimmy@dhemail.com"))
                .isEqualTo(new Email("JIMMY@dhemail.com"));
    }
}