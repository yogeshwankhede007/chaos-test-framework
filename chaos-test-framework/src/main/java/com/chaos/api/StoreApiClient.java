package com.chaos.api;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Slf4j
@Component
public class StoreApiClient {

    @Value("${test.api.url}")
    private String apiUrl;

    @Value("${test.api.timeout}")
    private int timeout;

    public Response getAllProducts() {
        log.info("Fetching all products from {}", apiUrl);
        return given()
                .baseUri(apiUrl)
                .timeout(timeout)
                .when()
                .get("/products")
                .then()
                .extract()
                .response();
    }

    public Response getProduct(int id) {
        log.info("Fetching product with id: {}", id);
        return given()
                .baseUri(apiUrl)
                .timeout(timeout)
                .pathParam("id", id)
                .when()
                .get("/products/{id}")
                .then()
                .extract()
                .response();
    }
}