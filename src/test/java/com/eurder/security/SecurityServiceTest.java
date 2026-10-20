package com.eurder.security;

import com.eurder.shared.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static com.eurder.security.TestPasswordEncoder.FAST_BCRYPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityServiceTest {

    private final UserRepository userRepository = new UserRepository();
    private final SecurityService securityService = new SecurityService(userRepository, FAST_BCRYPT);

    private User admin;
    private User customer;

    @BeforeEach
    void setUp() {
        admin = userRepository.save(User.create(UUID.randomUUID(), new Email("admin@eurder.com"),
                "adminSecret", Role.ADMIN, FAST_BCRYPT));
        customer = userRepository.save(User.create(UUID.randomUUID(), new Email("jimmy@dhemail.com"),
                "customerSecret", Role.CUSTOMER, FAST_BCRYPT));
    }

    @Test
    void validateAccess_withAnAuthorizedUser_returnsThatUser() {
        User user = securityService.validateAccess(basicHeader("admin@eurder.com:adminSecret"), Feature.CREATE_ITEM);

        assertThat(user).isSameAs(admin);
    }

    @Test
    void validateAccess_withEmailInAnotherCase_authenticatesTheUser() {
        User user = securityService.validateAccess(basicHeader("ADMIN@Eurder.com:adminSecret"), Feature.CREATE_ITEM);

        assertThat(user).isSameAs(admin);
    }

    @Test
    void validateAccess_withoutHeader_isUnauthorized() {
        assertThatThrownBy(() -> securityService.validateAccess(null, Feature.CREATE_ITEM))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void validateAccess_withUnknownEmail_isUnauthorized() {
        assertThatThrownBy(() -> securityService.validateAccess(
                basicHeader("nobody@eurder.com:adminSecret"), Feature.CREATE_ITEM))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Unknown email");
    }

    @Test
    void validateAccess_withWrongPassword_isUnauthorized() {
        assertThatThrownBy(() -> securityService.validateAccess(
                basicHeader("admin@eurder.com:wrongPassword"), Feature.CREATE_ITEM))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageStartingWith("Wrong password");
    }

    @Test
    void validateAccess_withMalformedEmail_isUnauthorized_notABadRequest() {
        assertThatThrownBy(() -> securityService.validateAccess(
                basicHeader("not-an-email:adminSecret"), Feature.CREATE_ITEM))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Malformed email in credentials");
    }

    @Test
    void validateAccess_withoutTheRequiredFeature_isForbidden() {
        assertThatThrownBy(() -> securityService.validateAccess(
                basicHeader("jimmy@dhemail.com:customerSecret"), Feature.CREATE_ITEM))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(customer.getId().toString())
                .hasMessageContaining("CREATE_ITEM");
    }

    private static String basicHeader(String emailAndPassword) {
        return "Basic " + Base64.getEncoder().encodeToString(emailAndPassword.getBytes(StandardCharsets.UTF_8));
    }
}