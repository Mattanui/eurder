package com.eurder.customer.domain;

import com.eurder.security.Role;
import com.eurder.security.User;
import com.eurder.security.UserRepository;
import com.eurder.shared.Email;

import static com.eurder.EurderTestClient.uniqueEmail;
import static com.eurder.security.TestPasswordEncoder.FAST_BCRYPT;

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

    public static Customer aPersistedCustomer(UserRepository userRepository, CustomerRepository customerRepository) {
        Customer customer = aCustomerWithEmail(uniqueEmail());
        userRepository.save(User.create(customer.getId(), customer.getEmail(), "aSecret", Role.CUSTOMER, FAST_BCRYPT));
        return customerRepository.save(customer);
    }
}