package com.eurder.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class TestPasswordEncoder {

    public static final PasswordEncoder FAST_BCRYPT = new BCryptPasswordEncoder(4);

    private TestPasswordEncoder() {
    }
}