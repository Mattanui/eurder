package com.eurder.item;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class CreateItemAcceptanceTest {

    @LocalServerPort
    private int port;

    @Value("${eurder.admin.email}")
    private String adminEmail;

    @Value("${eurder.admin.password}")
    private String adminPassword;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.config = RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(NumberReturnType.BIG_DECIMAL));
    }

    @Test
    void createItem_asAdmin_returnsCreatedItem() {
        given()
                .auth().preemptive().basic(adminEmail, adminPassword)
                .contentType(ContentType.JSON)
                .body(itemJson("699.95", 12))
                .when()
                .post("/items")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Fender Stratocaster"))
                .body("description", equalTo("Electric guitar with a maple neck"))
                .body("price", equalTo(new BigDecimal("699.95")))
                .body("stock", equalTo(12));
    }

    @Test
    void createItem_withoutCredentials_returnsUnauthorized() {
        given()
                .contentType(ContentType.JSON)
                .body(itemJson("699.95", 12))
                .when()
                .post("/items")
                .then()
                .statusCode(401)
                .header("WWW-Authenticate", containsString("Basic"))
                .body("detail", equalTo("Invalid or missing credentials"));
    }

    @Test
    void createItem_withWrongPassword_returnsUnauthorized() {
        given()
                .auth().preemptive().basic(adminEmail, "wrongPassword")
                .contentType(ContentType.JSON)
                .body(itemJson("699.95", 12))
                .when()
                .post("/items")
                .then()
                .statusCode(401)
                .body("detail", equalTo("Invalid or missing credentials"));
    }

    @Test
    void createItem_asCustomer_returnsForbidden() {
        String customerEmail = "jimmy." + UUID.randomUUID() + "@dhemail.com";
        registerCustomer(customerEmail, "customerSecret");

        given()
                .auth().preemptive().basic(customerEmail, "customerSecret")
                .contentType(ContentType.JSON)
                .body(itemJson("699.95", 12))
                .when()
                .post("/items")
                .then()
                .statusCode(403)
                .body("detail", equalTo("Access denied"));
    }

    @Test
    void createItem_withInvalidData_returnsBadRequestWithTheReason() {
        given()
                .auth().preemptive().basic(adminEmail, adminPassword)
                .contentType(ContentType.JSON)
                .body(itemJson("-1", 12))
                .when()
                .post("/items")
                .then()
                .statusCode(400)
                .body("detail", equalTo("Price must not be negative"));
    }

    @Test
    void createItem_withoutCredentials_logsTheUnauthorizedAccess(CapturedOutput output) {
        given()
                .contentType(ContentType.JSON)
                .body(itemJson("699.95", 12))
                .when()
                .post("/items")
                .then()
                .statusCode(401);

        assertThat(output).contains("Unauthorized access: Invalid or missing credentials");
    }

    private static String itemJson(String price, int stock) {
        return """
                {
                  "name": "Fender Stratocaster",
                  "description": "Electric guitar with a maple neck",
                  "price": %s,
                  "stock": %d
                }
                """.formatted(price, stock);
    }

    private static void registerCustomer(String email, String password) {
        String customerJson = """
                {
                  "firstName": "Jimmy",
                  "lastName": "Hendrix",
                  "email": "%s",
                  "password": "%s",
                  "address": {"street": "Rue de la guitare", "houseNumber": "10A", "postalCode": "1000",
                              "city": "Brussels", "country": "Belgium"},
                  "phoneNumber": {"countryCode": "+32", "number": "485652311"}
                }
                """.formatted(email, password);
        given().contentType(ContentType.JSON).body(customerJson).post("/customers")
                .then().statusCode(201);
    }
}