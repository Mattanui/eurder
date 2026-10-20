package com.eurder.customer.service;

import com.eurder.customer.domain.Customer;
import com.eurder.customer.domain.CustomerRepository;
import com.eurder.security.Role;
import com.eurder.security.User;
import com.eurder.security.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer registerCustomer(Customer customer, String rawPassword) {
        User user = User.create(customer.getId(), customer.getEmail(), rawPassword, Role.CUSTOMER, passwordEncoder);
        userRepository.save(user);
        Customer registeredCustomer = customerRepository.save(customer);
        log.info("Customer registered with id {}", registeredCustomer.getId());
        return registeredCustomer;
    }
}