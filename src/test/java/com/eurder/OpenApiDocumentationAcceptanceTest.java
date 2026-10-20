package com.eurder;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiDocumentationAcceptanceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void openApiDocumentation_isPublished_withEurderTitle() {
        given()
                .when()
                .get("/v3/api-docs")
                .then()
                .statusCode(200)
                .body("info.title", equalTo("Eurder API"));
    }

    @Test
    void openApiDocumentation_declaresTheBasicAuthenticationScheme() {
        given()
                .when()
                .get("/v3/api-docs")
                .then()
                .statusCode(200)
                .body("components.securitySchemes.basicAuth.type", equalTo("http"))
                .body("components.securitySchemes.basicAuth.scheme", equalTo("basic"));
    }
}