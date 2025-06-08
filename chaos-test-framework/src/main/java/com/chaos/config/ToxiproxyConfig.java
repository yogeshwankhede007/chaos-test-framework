package com.chaos.config;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ToxiproxyConfig {

    @Bean
    public ToxiproxyClient toxiproxyClient(ToxiproxyProperties properties) {
        return new ToxiproxyClient(properties.getHost(), properties.getPort());
    }

    @Bean
    public Proxy storeApiProxy(ToxiproxyClient client, ToxiproxyProperties properties) throws Exception {
        ToxiproxyProperties.ProxyConfig config = properties.getProxies().get("store-api");
        return client.createProxy("store-api", config.getListen(), config.getUpstream());
    }
}