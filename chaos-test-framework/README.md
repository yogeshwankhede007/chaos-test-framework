# README.md

# Chaos Test Framework

## Overview
The Chaos Test Framework is designed for chaos testing using Toxiproxy, allowing you to simulate various network conditions and service failures against the demo API at [Fake Store API](https://fakestoreapi.in/). This framework follows Behavior-Driven Development (BDD) principles to ensure comprehensive testing of real-time scenarios.

## Project Structure
```
chaos-test-framework
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── chaos
│   │   │           ├── config
│   │   │           │   ├── ToxiproxyConfig.java
│   │   │           │   └── TestConfig.java
│   │   │           ├── proxy
│   │   │           │   └── ProxyManager.java
│   │   │           ├── api
│   │   │           │   └── StoreApiClient.java
│   │   │           └── utils
│   │   │               └── TestUtils.java
│   └── test
│       ├── java
│       │   └── com
│       │       └── chaos
│       │           ├── features
│       │           │   ├── NetworkLatency.feature
│       │           │   └── ServiceFailure.feature
│       │           └── steps
│       │               └── ChaosTestSteps.java
│       └── resources
│           └── cucumber.properties
├── pom.xml
└── README.md
```

## Setup Instructions
1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd chaos-test-framework
   ```
3. Build the project using Maven:
   ```
   mvn clean install
   ```
4. Run the tests:
   ```
   mvn test
   ```

## Usage
- The framework allows you to define chaos scenarios in Gherkin syntax within the `.feature` files located in `src/test/java/com/chaos/features/`.
- Implement the step definitions in `ChaosTestSteps.java` to link the Gherkin steps to Java methods.
- Utilize the `StoreApiClient.java` to interact with the demo API and perform assertions based on the responses.

## Contributing
Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

## License
This project is licensed under the MIT License. See the LICENSE file for details.