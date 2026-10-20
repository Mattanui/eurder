package com.eurder.security;

import com.eurder.shared.Email;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import static com.eurder.security.TestPasswordEncoder.FAST_BCRYPT;
import static org.assertj.core.api.Assertions.assertThat;

class AdminAccountInitializerTest {

    @Test
    void run_createsTheAdminAccountFromTheConfiguration() {
        UserRepository userRepository = new UserRepository();
        AdminAccountInitializer initializer =
                new AdminAccountInitializer(userRepository, FAST_BCRYPT, "admin@eurder.com", "adminSecret");

        initializer.run(new DefaultApplicationArguments());

        User admin = userRepository.findByEmail(new Email("admin@eurder.com")).orElseThrow();
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(admin.passwordMatches("adminSecret", FAST_BCRYPT)).isTrue();
    }
}