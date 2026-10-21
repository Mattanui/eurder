package com.eurder;

import io.restassured.http.ContentType;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public final class EurderTestClient {

    private EurderTestClient() {
    }

    public static String uniqueEmail() {
        return "jimmy." + UUID.randomUUID() + "@dhemail.com";
    }

    public static String registerCustomer(String email, String password) {
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
        return given()
                .contentType(ContentType.JSON)
                .body(customerJson)
                .post("/customers")
                .then().statusCode(201)
                .extract().path("id");
    }

    public static String addItem(String adminEmail, String adminPassword, String name, String price, int stock) {
        String itemJson = """
                {
                  "name": "%s",
                  "description": "An item created by an acceptance test",
                  "price": %s,
                  "stock": %d
                }
                """.formatted(name, price, stock);
        return given()
                .auth().preemptive().basic(adminEmail, adminPassword)
                .contentType(ContentType.JSON)
                .body(itemJson)
                .post("/items")
                .then().statusCode(201)
                .extract().path("id");
    }
}