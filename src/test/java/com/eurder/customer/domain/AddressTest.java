package com.eurder.customer.domain;

import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressTest {

    @Test
    void createAddress_withValidValues_keepsThem() {
        Address address = new Address("Rue de la guitare", "10A", "1000", "Brussels", "Belgium");

        assertThat(address)
                .extracting(Address::street, Address::houseNumber, Address::postalCode,
                        Address::city, Address::country)
                .containsExactly("Rue de la guitare", "10A", "1000", "Brussels", "Belgium");
    }

    @Test
    void createAddress_withSurroundingSpaces_isTrimmed() {
        Address address = new Address(" Rue de la guitare ", " 10A", "1000 ", "  Brussels", "Belgium  ");

        assertThat(address)
                .extracting(Address::street, Address::houseNumber, Address::postalCode,
                        Address::city, Address::country)
                .containsExactly("Rue de la guitare", "10A", "1000", "Brussels", "Belgium");
    }

    @ParameterizedTest(name = "{5}")
    @MethodSource("addressesWithOneMissingField")
    void createAddress_withAMissingField_isRejected(String street, String houseNumber, String postalCode,
                                                    String city, String country, String expectedMessage) {
        assertThatThrownBy(() -> new Address(street, houseNumber, postalCode, city, country))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage(expectedMessage);
    }

    private static Stream<Arguments> addressesWithOneMissingField() {
        return Stream.of(
                Arguments.of(null, "10A", "1000", "Brussels", "Belgium", "Street must be provided"),
                Arguments.of("Rue de la guitare", "", "1000", "Brussels", "Belgium", "House number must be provided"),
                Arguments.of("Rue de la guitare", "10A", "   ", "Brussels", "Belgium", "Postal code must be provided"),
                Arguments.of("Rue de la guitare", "10A", "1000", null, "Belgium", "City must be provided"),
                Arguments.of("Rue de la guitare", "10A", "1000", "Brussels", "  ", "Country must be provided")
        );
    }
}