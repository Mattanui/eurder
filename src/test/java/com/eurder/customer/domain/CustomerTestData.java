package com.eurder.customer.domain;

import com.eurder.shared.Email;

public final class CustomerTestData {

    private CustomerTestData() {
    }

    public static Customer aCustomer() {
        return aCustomerWithEmail("jimmy.hendrix@dhemail.com");
    }

    public static Customer aCustomerWithEmail(String email) {
        return new Customer("Jimmy", "Hendrix",
                new Email(email),
                new Address("Rue de la guitare", "10A", "1000", "Brussels", "Belgium"),
                new PhoneNumber("+32", "485652311"));
    }
}