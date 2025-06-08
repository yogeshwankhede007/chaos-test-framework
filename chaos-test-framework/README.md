# 🌪 Chaos Testing Framework with Toxiproxy

> Enterprise-grade chaos engineering framework for resilience testing

[![Build Status](https://img.shields.io/travis/com/username/chaos-testing-toxiproxy/main)](https://travis-ci.com/username/chaos-testing-toxiproxy)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.12-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.11.0-green.svg)](https://cucumber.io/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 🎯 Overview

A sophisticated chaos engineering framework that leverages Toxiproxy to simulate various network conditions and validate application resilience. Built with Spring Boot and Cucumber, it provides a robust platform for conducting controlled chaos experiments.

## ✨ Key Features

### 🌐 Network Chaos Simulation
- **Latency Injection**: Simulate network delays
- **Bandwidth Throttling**: Test with limited bandwidth
- **Packet Loss**: Simulate unreliable networks
- **Connection Timeouts**: Test timeout handling
- **Network Partitions**: Simulate network splits

### 🔄 API Testing Integration
- **REST API Validation**: Comprehensive endpoint testing
- **CRUD Operations**: Complete lifecycle testing
- **Response Analysis**: Timing and content validation
- **Error Handling**: Graceful degradation testing

### 📊 Test Management
- **BDD Scenarios**: Human-readable test cases
- **Data-Driven Testing**: Parameterized scenarios
- **Parallel Execution**: Efficient test runs
- **Rich Reporting**: Detailed test analytics

## 🚀 Getting Started

### Prerequisites
```bash
# Check Java version
java --version  # Requires Java 17+

# Check Maven version
mvn --version   # Requires Maven 3.8+

# Check Docker version
docker --version
```

### Installation

```bash
# Clone the repository
git clone https://github.com/yogeshwankhede007/chaos-testing-toxiproxy.git
cd chaos-testing-toxiproxy

# Start Toxiproxy container
docker run -d \
  -p 8474:8474 \
  -p 8888:8888 \
  --name toxiproxy \
  ghcr.io/shopify/toxiproxy

# Build the project
mvn clean install
```

## 🔧 Configuration

### Application Properties
```yaml
toxiproxy:
  host: localhost
  port: 8474
  proxies:
    store-api:
      listen: "0.0.0.0:8888"
      upstream: "https://fakestoreapi.in"
```

## 💻 Usage Examples

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test categories
mvn test -Dcucumber.filter.tags="@api"
mvn test -Dcucumber.filter.tags="@chaos"

# Parallel execution
mvn test -Dcucumber.execution.parallel.enabled=true
```

### Example Scenario
```gherkin
@api @chaos
Scenario: Create product under network latency
  Given the Toxiproxy is set up
  When I introduce a latency of 1000 milliseconds
  And I create a new product with details:
    | name          | price |
    | Gaming Mouse  | 99.99 |
  Then the response time should be greater than 1000 milliseconds
  And the product should be created successfully
```

## 🏗 Project Structure

```plaintext
chaos-test-framework/
├── src/
│   ├── main/
│   │   ├── java/com/chaos/
│   │   │   ├── config/
│   │   │   ├── proxy/
│   │   │   └── api/
│   │   └── resources/
│   └── test/
│       ├── java/com/chaos/
│       │   ├── features/
│       │   └── steps/
│       └── resources/
└── pom.xml
```

## 📈 Test Reports

Generated reports available at:
- `target/cucumber-reports/index.html` - Cucumber HTML reports
- `target/cucumber-reports/cucumber.json` - JSON format reports
- `target/surefire-reports/` - JUnit XML reports

## 🛠 Tech Stack

- **Core Framework**: Spring Boot 2.7.12
- **Testing Framework**: Cucumber 7.11.0
- **Proxy Tool**: Toxiproxy
- **HTTP Client**: REST Assured 5.3.0
- **Build Tool**: Maven 3.8+
- **Logging**: SLF4J with Logback

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [@yogeshwankhede007](https://github.com/yogeshwankhede007)

---

<p align="center">
  <sub>Built with ❤️ for chaos engineering</sub>
</p>