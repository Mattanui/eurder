package com.eurder.customer;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class CreateCustomerAcceptanceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createCustomer_withValidData_returnsCreatedCustomerWithoutPassword() {
        String email = uniqueEmail();

        given()
                .contentType(ContentType.JSON)
                .body(customerJson(email))
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("Jimmy"))
                .body("lastName", equalTo("Hendrix"))
                .body("email", equalTo(email))
                .body("address.street", equalTo("Rue de la guitare"))
                .body("address.houseNumber", equalTo("10A"))
                .body("address.postalCode", equalTo("1000"))
                .body("address.city", equalTo("Brussels"))
                .body("address.country", equalTo("Belgium"))
                .body("phoneNumber.countryCode", equalTo("+32"))
                .body("phoneNumber.number", equalTo("485652311"))
                .body("$", not(hasKey("password")));
    }

    @Test
    void createCustomer_withInvalidData_returnsBadRequestWithTheReason() {
        given()
                .contentType(ContentType.JSON)
                .body(customerJson("not-an-email"))
                .when()
                .post("/customers")
                .then()
                .statusCode(400)
                .body("detail", equalTo("Email has an invalid format: not-an-email"));
    }

    @Test
    void createCustomer_withAnEmailAlreadyInUse_returnsConflict() {
        String email = uniqueEmail();
        given().contentType(ContentType.JSON).body(customerJson(email)).post("/customers")
                .then().statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(customerJson(email.toUpperCase()))
                .when()
                .post("/customers")
                .then()
                .statusCode(409)
                .body("detail", equalTo("Email address is already in use"));
    }

    @Test
    void createCustomer_withMalformedJson_returnsBadRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"firstName\": ")
                .when()
                .post("/customers")
                .then()
                .statusCode(400);
    }

    @Test
    void createCustomer_withInvalidData_logsTheError(CapturedOutput output) {
        given()
                .contentType(ContentType.JSON)
                .body(customerJson("still-not-an-email"))
                .when()
                .post("/customers")
                .then()
                .statusCode(400);

        assertThat(output).contains("Invalid input: Email has an invalid format: still-not-an-email");
    }

    private static String uniqueEmail() {
        return "jimmy." + UUID.randomUUID() + "@dhemail.com";
    }

    private static String customerJson(String email) {
        return """
                {
                  "firstName": "Jimmy",
                  "lastName": "Hendrix",
                  "email": "%s",
                  "password": "randomSecretPassword",
                  "address": {
                    "street": "Rue de la guitare",
                    "houseNumber": "10A",
                    "postalCode": "1000",
                    "city": "Brussels",
                    "country": "Belgium"
                  },
                  "phoneNumber": {
                    "countryCode": "+32",
                    "number": "485652311"
                  }
                }
                """.formatted(email);
    }
}