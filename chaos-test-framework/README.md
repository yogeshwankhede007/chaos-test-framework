# Chaos Testing Framework

<div align="center">

[![Build](https://github.com/chaos-testing/toxiproxy-framework/workflows/CI/badge.svg)](https://github.com/chaos-testing/toxiproxy-framework/actions)
[![Coverage](https://codecov.io/gh/chaos-testing/toxiproxy-framework/branch/main/graph/badge.svg)](https://codecov.io/gh/chaos-testing/toxiproxy-framework)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

A modern chaos engineering framework for testing system resilience with Toxiproxy

[Getting Started](#getting-started) · [Documentation](#documentation-) · [Examples](#examples) · [Contributing](#contributing)

</div>

## Overview

Test your application's resilience by introducing controlled network failures. Built with Spring Boot and Cucumber for readable, maintainable chaos experiments.

### Features

- **Network Simulation** - Latency, packet loss, bandwidth throttling
- **BDD Testing** - Human-readable test scenarios with Cucumber
- **REST API Testing** - Comprehensive endpoint validation
- **Observability** - Built-in metrics and monitoring
- **CI/CD Ready** - Easy integration with existing pipelines

## Tech Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Runtime environment |
| Spring Boot | 3.2.1 | Application framework |
| Cucumber | 7.11.0 | BDD testing |
| Toxiproxy | 2.5.0 | Network proxy |
| REST Assured | 5.3.0 | API testing |

## Getting Started

### Prerequisites

```bash
java --version    # Java 21+
mvn --version     # Maven 3.8+
docker --version  # Docker for Toxiproxy
```

### Quick Setup

1. **Clone and build**
   ```bash
   git clone https://github.com/yogeshwankhede007/chaos-testing-toxiproxy.git
   cd chaos-testing-toxiproxy
   mvn clean install
   ```

2. **Start Toxiproxy**
   ```bash
   docker run -d -p 8474:8474 -p 8888:8888 --name toxiproxy ghcr.io/shopify/toxiproxy
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

## Configuration

```yaml
# application.yml
toxiproxy:
  host: localhost
  port: 8474
  proxies:
    api-proxy:
      listen: "0.0.0.0:8888"
      upstream: "https://fakestoreapi.com"

logging:
  level:
    com.chaos: INFO
```

## Examples

### Basic Chaos Test

```gherkin
Scenario: API resilience under network latency
  Given the API proxy is configured
  When I add 500ms latency to all requests
  And I fetch product with ID 1
  Then the response should be successful
  And the response time should be greater than 500ms
```

### Advanced Network Simulation

```gherkin
Scenario: Handle packet loss gracefully
  Given the API proxy is active
  When I introduce 10% packet loss
  And I create a new product:
    | name  | price |
    | Mouse | 29.99 |
  Then the request should retry automatically
  And the product should be created successfully
```

## Project Structure

```
src/
├── main/java/com/chaos/
│   ├── config/          # Spring configuration
│   ├── proxy/           # Toxiproxy management
│   └── api/             # API clients
├── test/java/com/chaos/
│   ├── steps/           # Cucumber step definitions
│   └── features/        # Test scenarios
└── test/resources/
    └── features/        # Gherkin feature files
```

## Running Tests

```bash
# All tests
mvn test

# Specific tags
mvn test -Dcucumber.filter.tags="@api"
mvn test -Dcucumber.filter.tags="@chaos"

# Parallel execution
mvn test -Dcucumber.execution.parallel.enabled=true -Dcucumber.execution.parallel.config.strategy=dynamic
```

## Documentation 📚

- [API Reference](docs/api.md) - Complete API documentation
- [Configuration Guide](docs/configuration.md) - Setup and configuration options
- [Best Practices](docs/best-practices.md) - Chaos engineering guidelines
- [Troubleshooting](docs/troubleshooting.md) - Common issues and solutions

## Docker Support

```dockerfile
# Dockerfile
FROM openjdk:21-jdk-slim
COPY target/chaos-testing-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  chaos-framework:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - toxiproxy
  
  toxiproxy:
    image: ghcr.io/shopify/toxiproxy:2.5.0
    ports:
      - "8474:8474"
      - "8888:8888"
```

## Monitoring

The framework includes built-in metrics:

- Request/response times
- Error rates
- Chaos experiment results
- System resource usage

Access metrics at `http://localhost:8080/actuator/metrics`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Make your changes
4. Add tests for new functionality
5. Run the test suite (`mvn test`)
6. Commit your changes (`git commit -am 'Add new feature'`)
7. Push to the branch (`git push origin feature/new-feature`)
8. Create a Pull Request

### Development Guidelines

- Follow existing code style
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 🐛 **Issues**: [GitHub Issues](https://github.com/yogeshwankhede007/chaos-testing-toxiproxy/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/yogeshwankhede007/chaos-testing-toxiproxy/discussions)
- 📧 **Email**: tech.yogesh@protonmail.me

---

<div align="center">
  <sub>Built with ❤️ for reliable systems</sub>
</div>
