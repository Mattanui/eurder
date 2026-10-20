package com.eurder.security;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String reason) {
        super(reason);
    }
}