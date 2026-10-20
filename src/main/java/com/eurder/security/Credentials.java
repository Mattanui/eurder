package com.eurder.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record Credentials(String email, String password) {

    private static final String BASIC_SCHEME = "Basic ";

    public static Credentials fromBasicAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedException("Missing Authorization header");
        }
        if (!authorizationHeader.regionMatches(true, 0, BASIC_SCHEME, 0, BASIC_SCHEME.length())) {
            throw new UnauthorizedException("Authorization header does not use the Basic scheme");
        }
        String decoded = decodeBase64(authorizationHeader.substring(BASIC_SCHEME.length()).trim());
        int separatorIndex = decoded.indexOf(':');
        if (separatorIndex < 0) {
            throw new UnauthorizedException("Basic credentials must have the form email:password");
        }
        return new Credentials(decoded.substring(0, separatorIndex), decoded.substring(separatorIndex + 1));
    }

    private static String decodeBase64(String encoded) {
        try {
            return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedException("Basic credentials are not valid Base64");
        }
    }

    @Override
    public String toString() {
        return "Credentials[email=%s, password=****]".formatted(email);
    }
}