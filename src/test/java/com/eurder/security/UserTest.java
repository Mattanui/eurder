package com.eurder.security;

import com.eurder.shared.Email;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private static final PasswordEncoder PASSWORD_ENCODER = TestPasswordEncoder.FAST_BCRYPT;    private static final UUID ID = UUID.randomUUID();
    private static final Email EMAIL = new Email("jimmy.hendrix@dhemail.com");

    @Test
    void createUser_keepsIdEmailAndRole() {
        User user = User.create(ID, EMAIL, "aSecret", Role.CUSTOMER, PASSWORD_ENCODER);

        assertThat(user)
                .extracting(User::getId, User::getEmail, User::getRole)
                .containsExactly(ID, EMAIL, Role.CUSTOMER);
    }

    @Test
    void passwordMatches_withTheRightPassword_returnsTrue() {
        User user = User.create(ID, EMAIL, "aSecret", Role.CUSTOMER, PASSWORD_ENCODER);

        assertThat(user.passwordMatches("aSecret", PASSWORD_ENCODER)).isTrue();
    }

    @Test
    void passwordMatches_withAWrongPassword_returnsFalse() {
        User user = User.create(ID, EMAIL, "aSecret", Role.CUSTOMER, PASSWORD_ENCODER);

        assertThat(user.passwordMatches("anotherSecret", PASSWORD_ENCODER)).isFalse();
    }

    @Test
    void passwordMatches_withoutPassword_returnsFalse() {
        User user = User.create(ID, EMAIL, "aSecret", Role.CUSTOMER, PASSWORD_ENCODER);

        assertThat(user.passwordMatches(null, PASSWORD_ENCODER)).isFalse();
    }

    @Test
    void createUser_keepsSpacesInPassword() {
        User user = User.create(ID, EMAIL, " aSecret ", Role.CUSTOMER, PASSWORD_ENCODER);

        assertThat(user.passwordMatches(" aSecret ", PASSWORD_ENCODER)).isTrue();
        assertThat(user.passwordMatches("aSecret", PASSWORD_ENCODER)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createUser_withoutPassword_isRejected(String missingValue) {
        assertThatThrownBy(() -> User.create(ID, EMAIL, missingValue, Role.CUSTOMER, PASSWORD_ENCODER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Password must be provided");
    }

    @Test
    void createUser_withPasswordOfExactly72Bytes_isAccepted() {
        String password = "a".repeat(72);

        User user = User.create(ID, EMAIL, password, Role.CUSTOMER, PASSWORD_ENCODER);

        assertThat(user.passwordMatches(password, PASSWORD_ENCODER)).isTrue();
    }

    @Test
    void createUser_withPasswordLongerThan72Bytes_isRejected() {
        assertThatThrownBy(() -> User.create(ID, EMAIL, "a".repeat(73), Role.CUSTOMER, PASSWORD_ENCODER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Password must not exceed 72 bytes");
    }

    @Test
    void createUser_withAccentedPasswordOf37CharactersBut74Bytes_isRejected() {
        assertThatThrownBy(() -> User.create(ID, EMAIL, "é".repeat(37), Role.CUSTOMER, PASSWORD_ENCODER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Password must not exceed 72 bytes");
    }

    @Test
    void createUser_withoutId_isRejected() {
        assertThatThrownBy(() -> User.create(null, EMAIL, "aSecret", Role.CUSTOMER, PASSWORD_ENCODER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("User id must be provided");
    }

    @Test
    void createUser_withoutEmail_isRejected() {
        assertThatThrownBy(() -> User.create(ID, null, "aSecret", Role.CUSTOMER, PASSWORD_ENCODER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Email must be provided");
    }

    @Test
    void createUser_withoutRole_isRejected() {
        assertThatThrownBy(() -> User.create(ID, EMAIL, "aSecret", null, PASSWORD_ENCODER))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Role must be provided");
    }

    @Test
    void hasAccessTo_withAFeatureOfItsRole_returnsTrue() {
        User admin = User.create(ID, EMAIL, "aSecret", Role.ADMIN, PASSWORD_ENCODER);

        assertThat(admin.hasAccessTo(Feature.CREATE_ITEM)).isTrue();
    }

    @Test
    void hasAccessTo_withAFeatureOutsideItsRole_returnsFalse() {
        User customer = User.create(ID, EMAIL, "aSecret", Role.CUSTOMER, PASSWORD_ENCODER);

        assertThat(customer.hasAccessTo(Feature.CREATE_ITEM)).isFalse();
    }
}