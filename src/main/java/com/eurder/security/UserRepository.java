package com.eurder.security;

import com.eurder.shared.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailValue(String email);

    boolean existsByEmailValue(String email);

    default Optional<User> findByEmail(Email email) {
        return findByEmailValue(email.value());
    }

    default boolean existsByEmail(Email email) {
        return existsByEmailValue(email.value());
    }
}