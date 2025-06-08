Feature: Product API Operations Under Chaos Conditions

  Background:
    Given the Toxiproxy is set up for the Store API
    And the API endpoints are configured

  @api @create @latency
  Scenario: Create product with network latency
    When I introduce a latency of 1000 milliseconds
    And I create a new product with details:
      | name          | price | category     | description          |
      | Gaming Mouse  | 99.99 | Electronics  | High precision mouse |
    Then the response should be successful
    And the product should be created
    And the response time should be greater than 1000 milliseconds

  @api @read @packet-loss
  Scenario: Get product with packet loss simulation
    Given a product exists with id 1
    When I simulate 30% packet loss
    And I request the product details
    Then the system should retry the request
    And the response should be successful
    And the product details should match the database

  @api @update @bandwidth
  Scenario Outline: Update products with bandwidth limitation
    Given a product exists with id <id>
    When I limit the bandwidth to <bandwidth>Kbps
    And I update the product with new details:
      | name   | price   | category   |
      | <name> | <price> | <category> |
    Then the response should be successful
    And the product should be updated
    And the response time should be less than <timeout> milliseconds

    Examples:
      | id | bandwidth | name           | price  | category    | timeout |
      | 5  | 64       | Updated Mouse   | 149.99 | Electronics | 5000    |
      | 6  | 32       | Updated Keyboard| 199.99 | Electronics | 8000    |

  @api @patch @timeout
  Scenario: Patch product with connection timeout
    Given a product exists with id 7
    When I set the connection timeout to 2 seconds
    And I patch the product with details:
      | price  | description        |
      | 79.99  | Limited edition   |
    Then the system should handle the timeout gracefully
    And the changes should be consistent

  @api @delete @network-partition
  Scenario: Delete product during network partition
    Given a product exists with id 3
    When I simulate a network partition
    And I delete the product
    Then the system should queue the delete operation
    And retry until successful
    And the product should be removed

  @api @stress @mixed-chaos
  Scenario: Multiple operations under varying network conditions
    Given the following products exist:
      | id | name      | price  |
      | 1  | Product A | 29.99  |
      | 2  | Product B | 39.99  |
    When I execute the following chaos sequence:
      | chaos_type    | value  | duration_ms |
      | latency       | 500    | 2000       |
      | packet_loss   | 20     | 3000       |
      | bandwidth     | 128    | 2000       |
    And I perform multiple operations:
      | operation | id | data                    |
      | create    | -  | {"name": "New Product"} |
      | update    | 1  | {"price": 49.99}        |
      | delete    | 2  | -                       |
    Then all operations should complete successfully
    And the system should maintain data consistency