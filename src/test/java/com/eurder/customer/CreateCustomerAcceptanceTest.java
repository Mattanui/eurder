package com.eurder.customer;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateCustomerAcceptanceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createCustomer_withValidData_returnsCreatedCustomerWithoutPassword() {
        String requestBody = """
                {
                  "firstName": "Jimmy",
                  "lastName": "Hendrix",
                  "email": "jimmy.hendrix@dhemail.com",
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
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("Jimmy"))
                .body("lastName", equalTo("Hendrix"))
                .body("email", equalTo("jimmy.hendrix@dhemail.com"))
                .body("address.street", equalTo("Rue de la loi"))
                .body("address.houseNumber", equalTo("10A"))
                .body("address.postalCode", equalTo("1000"))
                .body("address.city", equalTo("Brussels"))
                .body("address.country", equalTo("Belgium"))
                .body("phoneNumber.countryCode", equalTo("+32"))
                .body("phoneNumber.number", equalTo("485652311"))
                .body("$", not(hasKey("password")));
    }
}