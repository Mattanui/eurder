package com.eurder.customer.service;

import com.eurder.customer.domain.Customer;
import com.eurder.customer.domain.CustomerRepository;
import com.eurder.security.EmailAlreadyInUseException;
import com.eurder.security.Role;
import com.eurder.security.User;
import com.eurder.security.UserRepository;
import com.eurder.shared.Email;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.eurder.customer.domain.CustomerTestData.aCustomer;
import static com.eurder.customer.domain.CustomerTestData.aCustomerWithEmail;
import static com.eurder.security.TestPasswordEncoder.FAST_BCRYPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerServiceTest {

    private final CustomerRepository customerRepository = new CustomerRepository();
    private final UserRepository userRepository = new UserRepository();
    private final CustomerService customerService =
            new CustomerService(customerRepository, userRepository, FAST_BCRYPT);

    @Test
    void registerCustomer_savesAndReturnsTheCustomer() {
        Customer customer = aCustomer();

        Customer registered = customerService.registerCustomer(customer, "aSecret");

        assertThat(registered).isSameAs(customer);
        assertThat(customerRepository.findById(customer.getId())).containsSame(customer);
    }

    @Test
    void registerCustomer_createsAUserThatCanLogIn_withTheSameIdAndCustomerRole() {
        Customer customer = aCustomer();

        customerService.registerCustomer(customer, "aSecret");

        User user = userRepository.findByEmail(customer.getEmail()).orElseThrow();
        assertThat(user.getId()).isEqualTo(customer.getId());
        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(user.passwordMatches("aSecret", FAST_BCRYPT)).isTrue();
    }

    @Test
    void registerCustomer_withAnEmailAlreadyInUse_isRejected_andSavesNoCustomer() {
        userRepository.save(User.create(UUID.randomUUID(), new Email("admin@eurder.com"),
                "adminSecret", Role.ADMIN, FAST_BCRYPT));
        Customer customer = aCustomerWithEmail("ADMIN@eurder.com");

        assertThatThrownBy(() -> customerService.registerCustomer(customer, "aSecret"))
                .isInstanceOf(EmailAlreadyInUseException.class);
        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }

    @Test
    void registerCustomer_withoutPassword_isRejected_andSavesNothing() {
        Customer customer = aCustomer();

        assertThatThrownBy(() -> customerService.registerCustomer(customer, "  "))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Password must be provided");
        assertThat(customerRepository.findById(customer.getId())).isEmpty();
        assertThat(userRepository.findByEmail(customer.getEmail())).isEmpty();
    }
}