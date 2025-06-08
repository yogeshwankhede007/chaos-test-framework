package com.chaos.api;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.params.CoreConnectionPNames;
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
        return createRequest()
                .when()
                .get("/products")
                .then()
                .extract()
                .response();
    }

    public Response getProduct(int id) {
        log.info("Fetching product with id: {}", id);
        return createRequest()
                .pathParam("id", id)
                .when()
                .get("/products/{id}")
                .then()
                .extract()
                .response();
    }

    public Response createProduct(Object productPayload) {
        log.info("Creating new product");
        return createRequest()
                .contentType(ContentType.JSON)
                .body(productPayload)
                .when()
                .post("/api/products")
                .then()
                .extract()
                .response();
    }

    public Response updateProduct(int id, Object productPayload) {
        log.info("Updating product with id: {}", id);
        return createRequest()
                .contentType(ContentType.JSON)
                .body(productPayload)
                .pathParam("id", id)
                .when()
                .put("/api/products/{id}")
                .then()
                .extract()
                .response();
    }

    public Response patchProduct(int id, Object patchPayload) {
        log.info("Patching product with id: {}", id);
        return createRequest()
                .contentType(ContentType.JSON)
                .body(patchPayload)
                .pathParam("id", id)
                .when()
                .patch("/api/products/{id}")
                .then()
                .extract()
                .response();
    }

    public Response deleteProduct(int id) {
        log.info("Deleting product with id: {}", id);
        return createRequest()
                .pathParam("id", id)
                .when()
                .delete("/api/products/{id}")
                .then()
                .extract()
                .response();
    }

    private RequestSpecification createRequest() {
        HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig()
                .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout)
                .setParam(CoreConnectionPNames.SO_TIMEOUT, timeout);

        RestAssuredConfig config = RestAssured.config()
                .httpClient(httpClientConfig);

        return given()
                .baseUri(apiUrl)
                .config(config);
    }
}