package com.eurder.security;

import com.eurder.PersistenceTest;
import com.eurder.shared.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;

import static com.eurder.security.TestPasswordEncoder.FAST_BCRYPT;
import static org.assertj.core.api.Assertions.assertThat;

@PersistenceTest
class AdminAccountInitializerTest {

    private static final String ADMIN_EMAIL = "another.admin@eurder.com";

    @Autowired
    private UserRepository userRepository;

    private AdminAccountInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new AdminAccountInitializer(userRepository, FAST_BCRYPT, ADMIN_EMAIL, "adminSecret");
    }

    @Test
    void run_createsTheAdminAccountFromTheConfiguration() {
        initializer.run(new DefaultApplicationArguments());

        User admin = userRepository.findByEmail(new Email(ADMIN_EMAIL)).orElseThrow();
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(admin.passwordMatches("adminSecret", FAST_BCRYPT)).isTrue();
    }

    @Test
    void run_whenTheAdminAlreadyExists_keepsTheExistingAccount() {
        initializer.run(new DefaultApplicationArguments());
        User firstAdmin = userRepository.findByEmail(new Email(ADMIN_EMAIL)).orElseThrow();
        long numberOfUsers = userRepository.count();

        initializer.run(new DefaultApplicationArguments());

        assertThat(userRepository.findByEmail(new Email(ADMIN_EMAIL)).orElseThrow().getId())
                .isEqualTo(firstAdmin.getId());
        assertThat(userRepository.count()).isEqualTo(numberOfUsers);
    }
}