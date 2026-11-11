package com.eurder.customer.service;

import com.eurder.PersistenceTest;
import com.eurder.customer.domain.Customer;
import com.eurder.customer.domain.CustomerRepository;
import com.eurder.security.EmailAlreadyInUseException;
import com.eurder.security.Role;
import com.eurder.security.User;
import com.eurder.security.UserRepository;
import com.eurder.shared.Email;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.UUID;

import static com.eurder.EurderTestClient.uniqueEmail;
import static com.eurder.customer.domain.CustomerTestData.aCustomerWithEmail;
import static com.eurder.security.TestPasswordEncoder.FAST_BCRYPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PersistenceTest
class CustomerServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository, userRepository, FAST_BCRYPT);
    }

    @Test
    void registerCustomer_savesAndReturnsTheCustomer() {
        Customer customer = aCustomerWithEmail(uniqueEmail());

        Customer registered = customerService.registerCustomer(customer, "aSecret");

        assertThat(registered).isEqualTo(customer);
        assertThat(customerRepository.findById(customer.getId())).contains(customer);
    }

    @Test
    void registerCustomer_createsAUserThatCanLogIn_withTheSameIdAndCustomerRole() {
        Customer customer = aCustomerWithEmail(uniqueEmail());

        customerService.registerCustomer(customer, "aSecret");

        User user = userRepository.findByEmail(customer.getEmail()).orElseThrow();
        assertThat(user.getId()).isEqualTo(customer.getId());
        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(user.passwordMatches("aSecret", FAST_BCRYPT)).isTrue();
    }

    @Test
    void registerCustomer_withAnEmailAlreadyInUse_isRejected_andSavesNoCustomer() {
        String existingEmail = uniqueEmail();
        userRepository.save(User.create(UUID.randomUUID(), new Email(existingEmail),
                "otherSecret", Role.CUSTOMER, FAST_BCRYPT));
        Customer customer = aCustomerWithEmail(existingEmail.toUpperCase(Locale.ROOT));

        assertThatThrownBy(() -> customerService.registerCustomer(customer, "aSecret"))
                .isInstanceOf(EmailAlreadyInUseException.class);
        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }

    @Test
    void registerCustomer_withoutPassword_isRejected_andSavesNothing() {
        Customer customer = aCustomerWithEmail(uniqueEmail());

        assertThatThrownBy(() -> customerService.registerCustomer(customer, "  "))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Password must be provided");
        assertThat(customerRepository.findById(customer.getId())).isEmpty();
        assertThat(userRepository.findByEmail(customer.getEmail())).isEmpty();
    }
}