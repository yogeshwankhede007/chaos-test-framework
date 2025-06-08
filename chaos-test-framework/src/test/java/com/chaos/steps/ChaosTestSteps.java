package com.chaos.steps;

import com.chaos.api.StoreApiClient;
import com.chaos.model.Product;
import com.chaos.proxy.ProxyManager;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.*;

@Slf4j
public class ChaosTestSteps {
    
    @Autowired
    private ProxyManager proxyManager;

    @Autowired
    private StoreApiClient apiClient;

    private Response apiResponse;
    private Product testProduct;
    private long startTime;
    private static final String PROXY_NAME = "store-api";
    private static final long DEFAULT_TIMEOUT = 5000;

    @Given("the Toxiproxy is set up")
    public void the_toxiproxy_is_set_up() {
        try {
            proxyManager.reset(PROXY_NAME);
            log.info("Toxiproxy setup completed successfully");
        } catch (Exception e) {
            log.error("Failed to set up Toxiproxy", e);
            throw new RuntimeException("Toxiproxy setup failed", e);
        }
    }

    @When("I send a request to the demo API")
    public void i_send_a_request_to_the_demo_api() {
        try {
            startTime = System.currentTimeMillis();
            apiResponse = apiClient.getAllProducts();
            log.info("API request sent successfully");
        } catch (Exception e) {
            log.error("Failed to send API request", e);
            throw new RuntimeException("API request failed", e);
        }
    }

    @Then("I should receive a valid response")
    public void i_should_receive_a_valid_response() {
        assertNotNull("Response should not be null", apiResponse);
        assertEquals("Response status code should be 200", 200, apiResponse.getStatusCode());
        assertFalse("Response body should not be empty", apiResponse.getBody().asString().isEmpty());
        log.info("Received valid response with status code: {}", apiResponse.getStatusCode());
    }

    @Given("the network latency is introduced")
    public void the_network_latency_is_introduced() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("latency", 2000L);
            params.put("jitter", 0.1f);
            proxyManager.simulateChaos(PROXY_NAME, ChaosType.LATENCY, params);
            log.info("Network latency introduced successfully");
        } catch (Exception e) {
            log.error("Failed to introduce network latency", e);
            throw new RuntimeException("Failed to introduce latency", e);
        }
    }

    @Then("the response time should be within acceptable limits")
    public void the_response_time_should_be_within_acceptable_limits() {
        long responseTime = System.currentTimeMillis() - startTime;
        assertTrue("Response time exceeded acceptable limit", responseTime < DEFAULT_TIMEOUT);
        log.info("Response time was {} ms", responseTime);
    }

    @Given("the service is down")
    public void the_service_is_down() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("down", true);
            proxyManager.simulateChaos(PROXY_NAME, ChaosType.FAILURE, params);
            log.info("Service down simulation activated");
        } catch (Exception e) {
            log.error("Failed to simulate service down", e);
            throw new RuntimeException("Failed to simulate service down", e);
        }
    }

    @Then("I should receive an error response")
    public void i_should_receive_an_error_response() {
        assertNotNull("Response should not be null", apiResponse);
        assertTrue("Status code should indicate error",
            apiResponse.getStatusCode() >= 500 && apiResponse.getStatusCode() < 600);
        log.info("Received error response with status code: {}", apiResponse.getStatusCode());
    }

    @When("I create a new product with details:")
    public void createProduct(DataTable dataTable) {
        Map<String, String> productData = dataTable.asMaps().get(0);
        testProduct = Product.builder()
                .name(productData.get("name"))
                .price(Double.parseDouble(productData.get("price")))
                .category(productData.get("category"))
                .build();

        startTime = System.currentTimeMillis();
        apiResponse = apiClient.createProduct(testProduct);
        log.info("Product creation response time: {} ms", System.currentTimeMillis() - startTime);
    }

    @When("I update the product with new details:")
    public void updateProduct(DataTable dataTable) {
        Map<String, String> updateData = dataTable.asMaps().get(0);
        Product updateProduct = Product.builder()
                .name(updateData.get("name"))
                .price(Double.parseDouble(updateData.get("price")))
                .build();

        startTime = System.currentTimeMillis();
        apiResponse = apiClient.updateProduct(testProduct.getId().intValue(), updateProduct);
        log.info("Product update response time: {} ms", System.currentTimeMillis() - startTime);
    }

    @When("I patch the product with details:")
    public void patchProduct(DataTable dataTable) {
        Map<String, String> patchData = dataTable.asMaps().get(0);
        startTime = System.currentTimeMillis();
        apiResponse = apiClient.patchProduct(testProduct.getId().intValue(), patchData);
        log.info("Product patch response time: {} ms", System.currentTimeMillis() - startTime);
    }

    @When("I delete the product")
    public void deleteProduct() {
        startTime = System.currentTimeMillis();
        apiResponse = apiClient.deleteProduct(testProduct.getId().intValue());
        log.info("Product deletion response time: {} ms", System.currentTimeMillis() - startTime);
    }

    @Then("the product should be created")
    public void verifyProductCreated() {
        assertEquals(201, apiResponse.getStatusCode());
        assertNotNull(apiResponse.jsonPath().getLong("id"));
        assertEquals(testProduct.getName(), apiResponse.jsonPath().getString("name"));
    }

    @Then("the product should be updated")
    public void verifyProductUpdated() {
        assertEquals(200, apiResponse.getStatusCode());
        assertEquals(testProduct.getId(), apiResponse.jsonPath().getLong("id"));
    }

    @Then("the product price should be updated")
    public void verifyProductPriceUpdated() {
        assertEquals(200, apiResponse.getStatusCode());
        assertEquals(testProduct.getPrice(), apiResponse.jsonPath().getDouble("price"), 0.01);
    }

    @Then("the product should be removed")
    public void verifyProductRemoved() {
        assertEquals(204, apiResponse.getStatusCode());
        apiResponse = apiClient.getProduct(testProduct.getId().intValue());
        assertEquals(404, apiResponse.getStatusCode());
    }

    @Given("a product exists with id {int}")
    public void productExists(int id) {
        apiResponse = apiClient.getProduct(id);
        assertEquals(200, apiResponse.getStatusCode());
        testProduct = Product.builder()
                .id((long) id)
                .name(apiResponse.jsonPath().getString("name"))
                .price(apiResponse.jsonPath().getDouble("price"))
                .category(apiResponse.jsonPath().getString("category"))
                .build();
    }

    @When("I introduce a latency of {int} milliseconds")
    public void introduceLatency(int latencyMs) {
        Map<String, Object> params = new HashMap<>();
        params.put("latency", (long) latencyMs);
        params.put("jitter", latencyMs * 0.1); // 10% jitter
        proxyManager.simulateChaos("store-api", ChaosType.LATENCY, params);
        log.info("Introduced latency of {}ms with {}ms jitter", latencyMs, latencyMs * 0.1);
    }

    @When("I simulate {int}% packet loss")
    public void simulatePacketLoss(int percentage) {
        Map<String, Object> params = new HashMap<>();
        params.put("percentage", percentage);
        proxyManager.simulateChaos("store-api", ChaosType.PACKET_LOSS, params);
        log.info("Simulated {}% packet loss", percentage);
    }

    @When("I limit the bandwidth to {int}Kbps")
    public void limitBandwidth(int kbps) {
        Map<String, Object> params = new HashMap<>();
        params.put("rate", kbps * 1024L); // Convert to bytes/s
        proxyManager.simulateChaos("store-api", ChaosType.BANDWIDTH, params);
        log.info("Limited bandwidth to {}Kbps", kbps);
    }

    @When("I set the connection timeout to {int} seconds")
    public void setConnectionTimeout(int seconds) {
        Map<String, Object> params = new HashMap<>();
        params.put("timeout", seconds * 1000L);
        proxyManager.simulateChaos("store-api", ChaosType.TIMEOUT, params);
        log.info("Set connection timeout to {}s", seconds);
    }

    @Then("the response time should be greater than {int} milliseconds")
    public void verifyMinimumResponseTime(int expectedMs) {
        long actualMs = System.currentTimeMillis() - startTime;
        assertTrue(String.format("Response time %dms should be greater than %dms", actualMs, expectedMs), 
                  actualMs > expectedMs);
        log.info("Response time {}ms exceeds minimum {}ms", actualMs, expectedMs);
    }

    @Then("the response time should be less than {int} milliseconds")
    public void verifyMaximumResponseTime(int expectedMs) {
        long actualMs = System.currentTimeMillis() - startTime;
        assertTrue(String.format("Response time %dms should be less than %dms", actualMs, expectedMs), 
                  actualMs < expectedMs);
        log.info("Response time {}ms is within maximum {}ms", actualMs, expectedMs);
    }

    @Then("the response should be successful")
    public void verifySuccessResponse() {
        assertTrue("Response status should be successful (2xx)", 
                  apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300);
        log.info("Received successful response with status code {}", apiResponse.getStatusCode());
    }

    @Then("the system should handle the timeout gracefully")
    public void verifyTimeoutHandling() {
        assertEquals("Timeout should return 504 Gateway Timeout", 504, apiResponse.getStatusCode());
        assertNotNull("Error message should be present", apiResponse.jsonPath().getString("error"));
        log.info("Timeout handled with status {} and message: {}", 
                 apiResponse.getStatusCode(), apiResponse.jsonPath().getString("error"));
    }
}