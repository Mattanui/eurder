package com.eurder.customer.domain;

import com.eurder.shared.Email;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    private static final Email EMAIL = new Email("jimmy.hendrix@dhemail.com");
    private static final Address ADDRESS = new Address("Rue de la guitare", "10A", "1000", "Brussels", "Belgium");
    private static final PhoneNumber PHONE_NUMBER = new PhoneNumber("+32", "485652311");

    @Test
    void createCustomer_withValidData_keepsTheData() {
        Customer customer = new Customer("Jimmy", "Hendrix", EMAIL, ADDRESS, PHONE_NUMBER);

        assertThat(customer)
                .extracting(Customer::getFirstName, Customer::getLastName, Customer::getEmail,
                        Customer::getAddress, Customer::getPhoneNumber)
                .containsExactly("Jimmy", "Hendrix", EMAIL, ADDRESS, PHONE_NUMBER);
    }

    @Test
    void createCustomer_receivesAGeneratedId() {
        Customer customer = new Customer("Jimmy", "Hendrix", EMAIL, ADDRESS, PHONE_NUMBER);

        assertThat(customer.getId()).isNotNull();
    }

    @Test
    void twoCustomers_withIdenticalData_areDifferentCustomers() {
        Customer first = new Customer("Jimmy", "Hendrix", EMAIL, ADDRESS, PHONE_NUMBER);
        Customer second = new Customer("Jimmy", "Hendrix", EMAIL, ADDRESS, PHONE_NUMBER);

        assertThat(first.getId()).isNotEqualTo(second.getId());
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void createCustomer_withSurroundingSpacesInNames_isTrimmed() {
        Customer customer = new Customer("  Jimmy ", " Hendrix  ", EMAIL, ADDRESS, PHONE_NUMBER);

        assertThat(customer)
                .extracting(Customer::getFirstName, Customer::getLastName)
                .containsExactly("Jimmy", "Hendrix");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createCustomer_withoutFirstName_isRejected(String missingValue) {
        assertThatThrownBy(() -> new Customer(missingValue, "Hendrix", EMAIL, ADDRESS, PHONE_NUMBER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("First name must be provided");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createCustomer_withoutLastName_isRejected(String missingValue) {
        assertThatThrownBy(() -> new Customer("Jimmy", missingValue, EMAIL, ADDRESS, PHONE_NUMBER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Last name must be provided");
    }

    @Test
    void createCustomer_withoutEmail_isRejected() {
        assertThatThrownBy(() -> new Customer("Jimmy", "Hendrix", null, ADDRESS, PHONE_NUMBER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Email must be provided");
    }

    @Test
    void createCustomer_withoutAddress_isRejected() {
        assertThatThrownBy(() -> new Customer("Jimmy", "Hendrix", EMAIL, null, PHONE_NUMBER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Address must be provided");
    }

    @Test
    void createCustomer_withoutPhoneNumber_isRejected() {
        assertThatThrownBy(() -> new Customer("Jimmy", "Hendrix", EMAIL, ADDRESS, null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Phone number must be provided");
    }
}