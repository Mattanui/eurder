package com.eurder.customer.domain;

import java.util.UUID;

import static com.eurder.shared.Validation.requireText;
import static com.eurder.shared.Validation.requireValue;

public class Customer {

    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final Email email;
    private final Address address;
    private final PhoneNumber phoneNumber;

    public Customer(String firstName, String lastName, Email email, Address address, PhoneNumber phoneNumber) {
        this.id = UUID.randomUUID();
        this.firstName = requireText(firstName, "First name");
        this.lastName = requireText(lastName, "Last name");
        this.email = requireValue(email, "Email");
        this.address = requireValue(address, "Address");
        this.phoneNumber = requireValue(phoneNumber, "Phone number");
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }
}