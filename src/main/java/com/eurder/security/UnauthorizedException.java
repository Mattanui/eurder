package com.eurder.security;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String reason) {
        super(reason);
    }
}