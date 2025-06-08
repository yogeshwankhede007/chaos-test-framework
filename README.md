# 🌪 Chaos Engineering with Toxiproxy

> A comprehensive guide to implementing chaos engineering using Toxiproxy

[![Chaos Engineering](https://img.shields.io/badge/Chaos-Engineering-red.svg)](https://principlesofchaos.org/)
[![Toxiproxy](https://img.shields.io/badge/Tool-Toxiproxy-blue.svg)](https://github.com/Shopify/toxiproxy)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.12-brightgreen.svg)](https://spring.io/projects/spring-boot)

## 📚 Table of Contents

- [Overview](#overview)
- [Chaos Testing Principles](#chaos-testing-principles)
- [Toxiproxy Features](#toxiproxy-features)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Usage](#usage)
- [Test Scenarios](#test-scenarios)
- [Best Practices](#best-practices)
- [Contributing](#contributing)

## 🎯 Overview

This project demonstrates comprehensive chaos testing using Toxiproxy to simulate various network conditions and validate system resilience. We focus on testing microservices' behavior under different network scenarios.

### Why Chaos Testing?

- Validate system resilience
- Discover potential failures before production
- Improve system reliability
- Build confidence in the system's capability
- Understand system behavior under stress

## 🔄 Chaos Testing Principles

1. **Start Small**
   - Begin with controlled experiments
   - Test in non-production environments first
   - Gradually increase complexity

2. **Define Steady State**
   - Establish baseline metrics
   - Monitor system behavior
   - Define success criteria

3. **Simulate Real-world Events**
   - Network latency
   - Service failures
   - Resource exhaustion
   - Data center outages

## 🛠 Toxiproxy Features

### Network Conditions We Can Simulate

| Toxic Type | Description | Use Case |
|------------|-------------|----------|
| Latency | Add delay to connections | Test timeout handling |
| Bandwidth | Limit network bandwidth | Test slow networks |
| Slow Close | Delay connection closure | Test connection pools |
| Timeout | Connection timeouts | Test retry mechanisms |
| Slicer | Slice TCP packages | Test package reassembly |
| Down | Drop all connections | Test circuit breakers |

### Implementation Example

```java
// Basic latency simulation
Proxy proxy = toxiproxy.createProxy("app", "localhost:26379", "localhost:6379");
proxy.toxics()
     .latency("latency-toxic", ToxicDirection.DOWNSTREAM, 1000)
     .setJitter(100);
```

## 📁 Project Structure

```plaintext
chaos-testing-toxiproxy/
├── chaos-test-framework/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   └── test/
│   │       └── java/
│   ├── pom.xml
│   └── README.md
├── docker/
│   └── docker-compose.yml
└── scripts/
    └── setup.sh
```

## 🚀 Installation

### Prerequisites

```bash
# Install Homebrew (if not installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 17
brew install openjdk@17

# Install Docker
brew install --cask docker
```

### Setup Toxiproxy

```bash
# Start Toxiproxy container
docker run -d \
  --name toxiproxy \
  -p 8474:8474 \
  -p 8888:8888 \
  ghcr.io/shopify/toxiproxy
```

## 💻 Usage

### Basic Commands

```bash
# Start the framework
./mvnw spring-boot:run

# Run tests
./mvnw test

# Generate reports
./mvnw verify
```

### Example Test Scenario

```gherkin
Scenario: Test service resilience under network latency
  Given the service is running
  When I introduce 1000ms latency
  Then the service should handle requests within 2000ms
  And no errors should occur
```

## 🔬 Test Scenarios

### 1. Network Latency Testing
- Simulate slow networks
- Test timeout configurations
- Validate retry mechanisms

### 2. Connection Failures
- Simulate network partitions
- Test circuit breakers
- Validate fallback mechanisms

### 3. Bandwidth Limitations
- Test large data transfers
- Validate streaming behavior
- Check timeout configurations

## ⚡ Best Practices

1. **Monitoring**
   - Always monitor system metrics
   - Set up proper logging
   - Track error rates

2. **Test Design**
   - Start with simple scenarios
   - Increase complexity gradually
   - Document all test cases

3. **Safety Measures**
   - Implement circuit breakers
   - Set proper timeouts
   - Use fallback mechanisms

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. Commit changes
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. Push branch
   ```bash
   git push origin feature/amazing-feature
   ```
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Contact

- **Author**: Yogesh Wankhede
- **Email**: [tech.yogesh@protonmail.me](mailto:your.email@example.com)
- **GitHub**: [@yogeshwankhede007](https://github.com/yogeshwankhede007)

---

<p align="center">Made with ❤️ for better system reliability</p>