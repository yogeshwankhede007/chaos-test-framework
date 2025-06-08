package com.chaos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Configuration
public class TestConfig {

    @Autowired
    private Environment environment;

    private static final String DEFAULT_ENV = "local";
    private static final String DEFAULT_PARAM = "default";

    @Bean
    public String environment() {
        String env = System.getenv("TEST_ENVIRONMENT");
        if (env == null || env.trim().isEmpty()) {
            log.warn("TEST_ENVIRONMENT not set, using default: {}", DEFAULT_ENV);
            env = DEFAULT_ENV;
        }
        log.info("Test environment configured as: {}", env);
        return env;
    }

    @Bean
    public String testParameter() {
        String param = System.getenv("TEST_PARAMETER");
        if (param == null || param.trim().isEmpty()) {
            log.warn("TEST_PARAMETER not set, using default: {}", DEFAULT_PARAM);
            param = DEFAULT_PARAM;
        }
        log.info("Test parameter configured as: {}", param);
        return param;
    }

    @Bean
    public TestProperties testProperties() {
        return new TestProperties(
            getPropertyWithDefault("test.api.url", "http://localhost:8080"),
            getPropertyWithDefault("test.api.timeout", "30000"),
            getPropertyWithDefault("test.proxy.host", "localhost"),
            getPropertyWithDefault("test.proxy.port", "8474")
        );
    }

    private String getPropertyWithDefault(String key, String defaultValue) {
        String value = environment.getProperty(key);
        if (value == null) {
            log.warn("{} not set, using default: {}", key, defaultValue);
            return defaultValue;
        }
        return value;
    }
}

@Slf4j
class TestProperties {
    private final String apiUrl;
    private final int timeout;
    private final String proxyHost;
    private final int proxyPort;

    public TestProperties(String apiUrl, String timeout, String proxyHost, String proxyPort) {
        this.apiUrl = apiUrl;
        this.timeout = Integer.parseInt(timeout);
        this.proxyHost = proxyHost;
        this.proxyPort = Integer.parseInt(proxyPort);
        log.info("Test properties initialized: url={}, timeout={}, proxy={}:{}", 
                apiUrl, timeout, proxyHost, proxyPort);
    }

    // Getters
    public String getApiUrl() { return apiUrl; }
    public int getTimeout() { return timeout; }
    public String getProxyHost() { return proxyHost; }
    public int getProxyPort() { return proxyPort; }
}