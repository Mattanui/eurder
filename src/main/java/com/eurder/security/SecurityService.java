package com.eurder.security;

import com.eurder.shared.Email;
import com.eurder.shared.InvalidInputException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User validateAccess(String authorizationHeader, Feature feature) {
        User user = authenticate(authorizationHeader);
        if (!user.hasAccessTo(feature)) {
            throw new ForbiddenException("User " + user.getId() + " has no access to feature " + feature);
        }
        return user;
    }

    private User authenticate(String authorizationHeader) {
        Credentials credentials = Credentials.fromBasicAuthorizationHeader(authorizationHeader);
        User user = userRepository.findByEmail(toEmail(credentials.email()))
                .orElseThrow(() -> new UnauthorizedException("Unknown email"));
        if (!user.passwordMatches(credentials.password(), passwordEncoder)) {
            throw new UnauthorizedException("Wrong password for user " + user.getId());
        }
        return user;
    }

    private static Email toEmail(String rawEmail) {
        try {
            return new Email(rawEmail);
        } catch (InvalidInputException exception) {
            throw new UnauthorizedException("Malformed email in credentials");
        }
    }
}