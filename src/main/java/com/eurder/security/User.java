package com.eurder.security;

import com.eurder.customer.domain.Customer;
import com.eurder.shared.Email;
import com.eurder.shared.InvalidInputException;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.eurder.shared.Validation.requireNonBlank;
import static com.eurder.shared.Validation.requireValue;

@Entity
@Table(name = "app_user")
public class User {

    private static final int MAX_PASSWORD_BYTES = 72;

    @Id
    private UUID id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email"))
    private Email email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private Role role;

    protected User() {

    }

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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof User that && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}