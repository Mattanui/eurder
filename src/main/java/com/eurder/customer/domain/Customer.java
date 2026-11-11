package com.eurder.customer.domain;

import com.eurder.shared.Email;
import jakarta.persistence.*;

import java.util.UUID;

import static com.eurder.shared.Validation.requireText;
import static com.eurder.shared.Validation.requireValue;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    private UUID id;

    private String firstName;

    private String lastName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email"))
    private Email email;

    @Embedded
    private Address address;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "countryCode", column = @Column(name = "phone_country_code")),
            @AttributeOverride(name = "number", column = @Column(name = "phone_number"))
    })
    private PhoneNumber phoneNumber;

    protected Customer() {

    }

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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof Customer that && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}