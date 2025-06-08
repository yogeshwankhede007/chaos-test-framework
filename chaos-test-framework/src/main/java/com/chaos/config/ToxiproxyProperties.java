package com.chaos.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "toxiproxy")
public class ToxiproxyProperties {
    private String host;
    private int port;
    private Map<String, ProxyConfig> proxies = new HashMap<>();

    @Data
    public static class ProxyConfig {
        private String listen;
        private String upstream;
    }
}