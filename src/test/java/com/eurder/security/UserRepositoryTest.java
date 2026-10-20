package com.eurder.security;

import com.eurder.shared.Email;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRepositoryTest {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder(4);

    private final UserRepository repository = new UserRepository();

    @Test
    void save_thenFindByEmail_returnsTheSavedUser() {
        User user = aUserWithEmail("jimmy.hendrix@dhemail.com");

        repository.save(user);

        assertThat(repository.findByEmail(new Email("jimmy.hendrix@dhemail.com"))).containsSame(user);
    }

    @Test
    void findByEmail_isCaseInsensitive() {
        User user = aUserWithEmail("jimmy.hendrix@dhemail.com");
        repository.save(user);

        assertThat(repository.findByEmail(new Email("Jimmy.HENDRIX@DHEmail.com"))).containsSame(user);
    }

    @Test
    void findByEmail_withUnknownEmail_returnsEmpty() {
        assertThat(repository.findByEmail(new Email("nobody@dhemail.com"))).isEmpty();
    }

    @Test
    void save_withAnEmailAlreadyInUse_isRejected_andKeepsTheFirstUser() {
        User first = aUserWithEmail("jimmy.hendrix@dhemail.com");
        User second = aUserWithEmail("JIMMY.HENDRIX@dhemail.com");
        repository.save(first);

        assertThatThrownBy(() -> repository.save(second))
                .isInstanceOf(EmailAlreadyInUseException.class)
                .hasMessage("Email address is already in use");
        assertThat(repository.findByEmail(new Email("jimmy.hendrix@dhemail.com"))).containsSame(first);
    }

    private static User aUserWithEmail(String email) {
        return User.create(UUID.randomUUID(), new Email(email), "aSecret", Role.CUSTOMER, PASSWORD_ENCODER);
    }
}