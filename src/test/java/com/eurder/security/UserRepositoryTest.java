package com.eurder.security;

import com.eurder.PersistenceTest;
import com.eurder.shared.Email;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static com.eurder.security.TestPasswordEncoder.FAST_BCRYPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PersistenceTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void save_thenFindByEmail_readsTheUserBackFromTheDatabase() {
        User user = aUserWithEmail("jimmy.hendrix@dhemail.com");
        repository.save(user);
        entityManager.flush();
        entityManager.clear();

        User found = repository.findByEmail(new Email("jimmy.hendrix@dhemail.com")).orElseThrow();

        assertThat(found).isNotSameAs(user);
        assertThat(found).isEqualTo(user);
        assertThat(found.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(found.passwordMatches("aSecret", FAST_BCRYPT)).isTrue();
    }

    @Test
    void findByEmail_isCaseInsensitive() {
        User user = aUserWithEmail("jimmy.hendrix@dhemail.com");
        repository.save(user);

        assertThat(repository.findByEmail(new Email("Jimmy.HENDRIX@DHEmail.com"))).contains(user);
    }

    @Test
    void findByEmail_withUnknownEmail_returnsEmpty() {
        assertThat(repository.findByEmail(new Email("nobody@dhemail.com"))).isEmpty();
    }

    @Test
    void existsByEmail_tellsWhetherTheEmailIsTaken() {
        repository.save(aUserWithEmail("jimmy.hendrix@dhemail.com"));

        assertThat(repository.existsByEmail(new Email("JIMMY.hendrix@dhemail.com"))).isTrue();
        assertThat(repository.existsByEmail(new Email("nobody@dhemail.com"))).isFalse();
    }

    @Test
    void save_withAnEmailAlreadyInUse_isRejectedByTheUniqueConstraint() {
        repository.saveAndFlush(aUserWithEmail("jimmy.hendrix@dhemail.com"));
        User second = aUserWithEmail("JIMMY.HENDRIX@dhemail.com");

        assertThatThrownBy(() -> repository.saveAndFlush(second))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private static User aUserWithEmail(String email) {
        return User.create(UUID.randomUUID(), new Email(email), "aSecret", Role.CUSTOMER, FAST_BCRYPT);
    }
}