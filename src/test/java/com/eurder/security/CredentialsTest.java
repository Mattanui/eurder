package com.eurder.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CredentialsTest {

    @Test
    void fromBasicAuthorizationHeader_withValidHeader_extractsEmailAndPassword() {
        Credentials credentials = Credentials.fromBasicAuthorizationHeader(basicHeader("jimmy@dhemail.com:aSecret"));

        assertThat(credentials).isEqualTo(new Credentials("jimmy@dhemail.com", "aSecret"));
    }

    @Test
    void fromBasicAuthorizationHeader_withColonInPassword_keepsTheWholePassword() {
        Credentials credentials = Credentials.fromBasicAuthorizationHeader(basicHeader("jimmy@dhemail.com:a:Secret"));

        assertThat(credentials.password()).isEqualTo("a:Secret");
    }

    @Test
    void fromBasicAuthorizationHeader_withLowercaseScheme_isAccepted() {
        String header = "basic " + encode("jimmy@dhemail.com:aSecret");

        assertThat(Credentials.fromBasicAuthorizationHeader(header).email()).isEqualTo("jimmy@dhemail.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void fromBasicAuthorizationHeader_withoutHeader_isRejected(String missingHeader) {
        assertThatThrownBy(() -> Credentials.fromBasicAuthorizationHeader(missingHeader))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Missing Authorization header");
    }

    @Test
    void fromBasicAuthorizationHeader_withAnotherScheme_isRejected() {
        assertThatThrownBy(() -> Credentials.fromBasicAuthorizationHeader("Bearer abc.def.ghi"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Authorization header does not use the Basic scheme");
    }

    @Test
    void fromBasicAuthorizationHeader_withInvalidBase64_isRejected() {
        assertThatThrownBy(() -> Credentials.fromBasicAuthorizationHeader("Basic !!!"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Basic credentials are not valid Base64");
    }

    @Test
    void fromBasicAuthorizationHeader_withoutColon_isRejected() {
        assertThatThrownBy(() -> Credentials.fromBasicAuthorizationHeader(basicHeader("jimmy@dhemail.com")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Basic credentials must have the form email:password");
    }

    @Test
    void toString_doesNotRevealThePassword() {
        Credentials credentials = new Credentials("jimmy@dhemail.com", "aSecret");

        assertThat(credentials.toString())
                .doesNotContain("aSecret")
                .contains("password=****");
    }

    private static String basicHeader(String emailAndPassword) {
        return "Basic " + encode(emailAndPassword);
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}