package com.eurder.security;

import com.eurder.shared.Email;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    private final Map<Email, User> usersByEmail = new ConcurrentHashMap<>();

    public User save(User user) {
        User alreadyRegistered = usersByEmail.putIfAbsent(user.getEmail(), user);
        if (alreadyRegistered != null) {
            throw new EmailAlreadyInUseException();
        }
        return user;
    }

    public Optional<User> findByEmail(Email email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }
}