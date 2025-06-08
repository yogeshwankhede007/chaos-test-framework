Feature: Network Latency Testing

  Background:
    Given the Toxiproxy is set up for the Store API

  @smoke
  Scenario: Simulate basic network latency
    When I introduce a latency of 200 milliseconds
    And I send a request to the Store API
    Then the response time should be greater than 200 milliseconds
    And the response should be successful

  @regression
  Scenario Outline: Validate response under different latency conditions
    When I introduce a latency of <latency> milliseconds
    And I send a request to the Store API
    Then the response time should be greater than <latency> milliseconds
    And the response should contain valid data
    And the response time should be less than <timeout> milliseconds

    Examples:
      | latency | timeout |
      | 500     | 2000    |
      | 1000    | 3000    |
      | 2000    | 5000    |

  @high-latency
  Scenario: Verify system behavior under extreme latency
    When I introduce a latency of 5000 milliseconds
    And I send a request to the Store API
    Then the request should timeout
    And the system should handle the timeout gracefully

  @jitter
  Scenario: Test with network jitter
    When I introduce a latency of 300 milliseconds with 50% jitter
    And I send multiple requests to the Store API
    Then all responses should be successful
    And response times should vary between 150 and 450 milliseconds

  @recovery
  Scenario: Verify system recovery after latency removal
    When I introduce a latency of 1000 milliseconds
    And I send a request to the Store API
    Then the response time should be greater than 1000 milliseconds
    When I remove all network latency
    And I send a request to the Store API
    Then the response time should be less than 100 milliseconds

  @concurrent
  Scenario: Handle multiple requests under latency
    When I introduce a latency of 500 milliseconds
    And I send 10 concurrent requests to the Store API
    Then all requests should complete successfully
    And average response time should be greater than 500 milliseconds
    And no request should timeout

  @variable-latency
  Scenario: Test with varying latency conditions
    When I introduce a latency pattern:
      | duration | latency |
      | 10       | 200     |
      | 20       | 500     |
      | 10       | 1000    |
    And I monitor the API for 1 minute
    Then all requests should complete successfully
    And response times should match the introduced latency pattern