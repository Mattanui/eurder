package com.eurder.security;

import com.eurder.shared.Email;
import com.eurder.shared.InvalidInputException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.eurder.shared.Validation.requireNonBlank;
import static com.eurder.shared.Validation.requireValue;

public class User {

    private static final int MAX_PASSWORD_BYTES = 72;

    private final UUID id;
    private final Email email;
    private final String passwordHash;
    private final Role role;

    private User(UUID id, Email email, String passwordHash, Role role) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public static User create(UUID id, Email email, String rawPassword, Role role, PasswordEncoder passwordEncoder) {
        requireValue(id, "User id");
        requireValue(email, "Email");
        requireValue(role, "Role");
        requireNonBlank(rawPassword, "Password");
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new InvalidInputException("Password must not exceed " + MAX_PASSWORD_BYTES + " bytes");
        }
        return new User(id, email, passwordEncoder.encode(rawPassword), role);
    }

    public boolean passwordMatches(String rawPassword, PasswordEncoder passwordEncoder) {
        return rawPassword != null && passwordEncoder.matches(rawPassword, passwordHash);
    }

    public boolean hasAccessTo(Feature feature) {
        return role.hasAccessTo(feature);
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}