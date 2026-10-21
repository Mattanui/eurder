package com.eurder.item;

import com.eurder.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static com.eurder.EurderTestClient.registerCustomer;
import static com.eurder.EurderTestClient.uniqueEmail;
import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@AcceptanceTest
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
        String customerEmail = uniqueEmail();
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


}