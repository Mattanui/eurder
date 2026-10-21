package com.eurder.eurder;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static com.eurder.EurderTestClient.addItem;
import static com.eurder.EurderTestClient.registerCustomer;
import static com.eurder.EurderTestClient.uniqueEmail;
import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderItemsAcceptanceTest {

    private static final String CUSTOMER_PASSWORD = "customerSecret";

    @LocalServerPort
    private int port;

    @Value("${eurder.admin.email}")
    private String adminEmail;

    @Value("${eurder.admin.password}")
    private String adminPassword;

    private String customerEmail;
    private String customerId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.config = RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(NumberReturnType.BIG_DECIMAL));
        customerEmail = uniqueEmail();
        customerId = registerCustomer(customerEmail, CUSTOMER_PASSWORD);
    }

    @Test
    void orderItems_asCustomer_returnsTheOrderWithShippingDatesAndTotalPrice() {
        String guitarId = addItem(adminEmail, adminPassword, "Fender Stratocaster", "699.95", 10);
        String strapId = addItem(adminEmail, adminPassword, "Leather strap", "49.99", 1);

        Response response = given()
                .auth().preemptive().basic(customerEmail, CUSTOMER_PASSWORD)
                .contentType(ContentType.JSON)
                .body(orderJson(itemGroupJson(guitarId, 2), itemGroupJson(strapId, 3)))
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("customerId", equalTo(customerId))
                .body("itemGroups", hasSize(2))
                .body("itemGroups[0].itemId", equalTo(guitarId))
                .body("itemGroups[0].itemName", equalTo("Fender Stratocaster"))
                .body("itemGroups[0].unitPrice", equalTo(new BigDecimal("699.95")))
                .body("itemGroups[0].amount", equalTo(2))
                .body("itemGroups[0].totalPrice", equalTo(new BigDecimal("1399.90")))
                .body("itemGroups[1].itemId", equalTo(strapId))
                .body("itemGroups[1].itemName", equalTo("Leather strap"))
                .body("itemGroups[1].totalPrice", equalTo(new BigDecimal("149.97")))
                .body("totalPrice", equalTo(new BigDecimal("1549.87")))
                .extract().response();

        LocalDate orderDate = LocalDate.parse(response.path("orderDate"));
        assertThat(response.<String>path("itemGroups[0].shippingDate"))
                .isEqualTo(orderDate.plusDays(1).toString());
        assertThat(response.<String>path("itemGroups[1].shippingDate"))
                .isEqualTo(orderDate.plusDays(7).toString());
    }

    @Test
    void orderItems_withoutCredentials_returnsUnauthorized() {
        String guitarId = addItem(adminEmail, adminPassword, "Fender Stratocaster", "699.95", 10);

        given()
                .contentType(ContentType.JSON)
                .body(orderJson(itemGroupJson(guitarId, 1)))
                .when()
                .post("/orders")
                .then()
                .statusCode(401);
    }

    @Test
    void orderItems_asAdmin_returnsForbidden() {
        String guitarId = addItem(adminEmail, adminPassword, "Fender Stratocaster", "699.95", 10);

        given()
                .auth().preemptive().basic(adminEmail, adminPassword)
                .contentType(ContentType.JSON)
                .body(orderJson(itemGroupJson(guitarId, 1)))
                .when()
                .post("/orders")
                .then()
                .statusCode(403);
    }

    @Test
    void orderItems_withAnUnknownItem_returnsBadRequest() {
        String unknownItemId = UUID.randomUUID().toString();

        given()
                .auth().preemptive().basic(customerEmail, CUSTOMER_PASSWORD)
                .contentType(ContentType.JSON)
                .body(orderJson(itemGroupJson(unknownItemId, 1)))
                .when()
                .post("/orders")
                .then()
                .statusCode(400)
                .body("detail", equalTo("Item not found: " + unknownItemId));
    }

    @Test
    void orderItems_withoutItemGroups_returnsBadRequest() {
        given()
                .auth().preemptive().basic(customerEmail, CUSTOMER_PASSWORD)
                .contentType(ContentType.JSON)
                .body(orderJson())
                .when()
                .post("/orders")
                .then()
                .statusCode(400)
                .body("detail", equalTo("An order must contain at least one item group"));
    }

    private static String itemGroupJson(String itemId, int amount) {
        return "{\"itemId\": \"%s\", \"amount\": %d}".formatted(itemId, amount);
    }

    private static String orderJson(String... itemGroups) {
        return "{\"itemGroups\": [%s]}".formatted(String.join(", ", itemGroups));
    }
}