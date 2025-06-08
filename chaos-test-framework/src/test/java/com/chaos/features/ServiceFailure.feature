Feature: Service Failure Testing

  Background:
    Given the Store API service is configured with Toxiproxy

  @smoke @service-down
  Scenario: Simulate complete service failure
    Given the service is running
    When I trigger a complete service failure
    Then I should receive a 503 Service Unavailable response
    And the error message should contain "Service Temporarily Unavailable"
    And client should implement proper retry mechanism

  @regression @partial-failure
  Scenario Outline: Handle different types of service failures
    Given the service is running normally
    When I simulate a "<failure_type>" failure
    Then the system should respond with "<expected_status>"
    And implement "<recovery_strategy>"

    Examples:
      | failure_type     | expected_status | recovery_strategy        |
      | database_timeout | 504            | circuit_breaker          |
      | memory_overflow  | 503            | service_restart          |
      | cpu_throttle     | 500            | load_balancer_failover   |
      | network_partition| 502            | connection_pool_refresh  |

  @failover
  Scenario: Validate failover mechanism
    Given the primary service instance is running
    And the backup service instance is available
    When I trigger a failure in the primary service
    Then the traffic should be redirected to backup service
    And users should experience minimal disruption
    And system logs should indicate failover event

  @recovery @high-priority
  Scenario: Service recovery with data consistency
    Given the service has failed due to network partition
    When I restore the network connectivity
    Then the service should recover automatically
    And all pending transactions should be processed
    And data should be consistent across all nodes

  @gradual-degradation
  Scenario: Handle gradual service degradation
    Given the service is running under normal conditions
    When I introduce increasing error rates:
      | duration_seconds | error_percentage |
      | 30              | 10               |
      | 30              | 25               |
      | 30              | 50               |
    Then the system should maintain partial functionality
    And critical operations should be prioritized
    And non-critical operations should be gracefully degraded

  @resilience
  Scenario: Test service resilience under cascading failures
    Given multiple dependent services are running
    When I trigger failures in the following order:
      | service    | failure_type |
      | database   | connection   |
      | cache      | overflow     |
      | auth       | timeout      |
    Then the system should handle failures gracefully
    And implement circuit breakers where appropriate
    And maintain core functionality
    And log all failure events for analysis

  @monitoring
  Scenario: Verify monitoring and alerting during failure
    Given the monitoring system is active
    When I trigger a critical service failure
    Then alerts should be generated within 30 seconds
    And appropriate teams should be notified
    And system metrics should be recorded for post-mortem
    And recovery procedures should be initiated automatically

  @chaos-engineering
  Scenario: Controlled chaos testing during off-peak hours
    Given the service is in maintenance window
    When I execute the following chaos experiments:
      | experiment_type | duration_minutes |
      | process_kill   | 5                |
      | network_delay  | 10               |
      | memory_leak    | 15               |
    Then all failures should be detected
    And system should recover automatically
    And no data loss should occur
    And performance metrics should be within SLA