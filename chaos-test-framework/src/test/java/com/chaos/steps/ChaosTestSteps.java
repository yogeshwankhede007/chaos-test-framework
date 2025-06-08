package com.chaos.steps;

import com.chaos.api.StoreApiClient;
import com.chaos.proxy.ProxyManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Slf4j
public class ChaosTestSteps {
    
    @Autowired
    private ProxyManager proxyManager;

    @Autowired
    private StoreApiClient apiClient;

    private Response apiResponse;
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
}