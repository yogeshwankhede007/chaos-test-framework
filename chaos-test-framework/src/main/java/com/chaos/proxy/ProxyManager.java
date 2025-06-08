package com.chaos.proxy;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.chaos.config.ToxiproxyProperties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Component
public class ProxyManager {
    
    private final ToxiproxyClient toxiproxyClient;
    private final ToxiproxyProperties properties;
    private final Map<String, Proxy> activeProxies;

    public ProxyManager(ToxiproxyProperties properties) {
        this.properties = properties;
        this.toxiproxyClient = new ToxiproxyClient(properties.getHost(), properties.getPort());
        this.activeProxies = new ConcurrentHashMap<>();
        initializeProxies();
    }

    public Proxy createProxy(String proxyName, String listen, String upstream) {
        try {
            Proxy proxy = toxiproxyClient.createProxy(proxyName, listen, upstream);
            activeProxies.put(proxyName, proxy);
            log.info("Created proxy: {} listening on {} forwarding to {}", proxyName, listen, upstream);
            return proxy;
        } catch (IOException e) {
            log.error("Failed to create proxy: {}", proxyName, e);
            throw new RuntimeException("Failed to create proxy", e);
        }
    }

    public void addLatency(String proxyName, long latencyMs) {
        try {
            Proxy proxy = getProxy(proxyName);
            proxy.toxics()
                 .latency("latency_downstream", ToxicDirection.DOWNSTREAM, latencyMs)
                 .setJitter(latencyMs / 10)
                 .setToxicity(1.0f);
            log.info("Added latency of {}ms to proxy: {}", latencyMs, proxyName);
        } catch (IOException e) {
            log.error("Failed to add latency to proxy: {}", proxyName, e);
            throw new RuntimeException("Failed to add latency", e);
        }
    }

    public void simulateNetworkFailure(String proxyName) {
        try {
            Proxy proxy = getProxy(proxyName);
            proxy.disable();
            log.info("Simulated network failure for proxy: {}", proxyName);
        } catch (IOException e) {
            log.error("Failed to simulate network failure for proxy: {}", proxyName, e);
            throw new RuntimeException("Failed to simulate network failure", e);
        }
    }

    public void addBandwidthLimitation(String proxyName, long rate) {
        try {
            Proxy proxy = getProxy(proxyName);
            proxy.toxics()
                 .bandwidth("bandwidth_downstream", ToxicDirection.DOWNSTREAM, rate);
            log.info("Added bandwidth limitation of {} bytes/s to proxy: {}", rate, proxyName);
        } catch (IOException e) {
            log.error("Failed to add bandwidth limitation to proxy: {}", proxyName, e);
            throw new RuntimeException("Failed to add bandwidth limitation", e);
        }
    }

    public void resetProxy(String proxyName) {
        try {
            Proxy proxy = getProxy(proxyName);
            proxy.enable();
            proxy.toxics().getAll().forEach(toxic -> {
                try {
                    toxic.remove();
                } catch (IOException e) {
                    log.error("Failed to remove toxic from proxy: {}", proxyName, e);
                }
            });
            log.info("Reset proxy: {}", proxyName);
        } catch (IOException e) {
            log.error("Failed to reset proxy: {}", proxyName, e);
            throw new RuntimeException("Failed to reset proxy", e);
        }
    }

    public void teardownProxy(String proxyName) {
        try {
            Proxy proxy = getProxy(proxyName);
            proxy.delete();
            activeProxies.remove(proxyName);
            log.info("Removed proxy: {}", proxyName);
        } catch (IOException e) {
            log.error("Failed to teardown proxy: {}", proxyName, e);
            throw new RuntimeException("Failed to teardown proxy", e);
        }
    }

    public Map<String, Proxy> listProxies() {
        try {
            Map<String, Proxy> proxies = new HashMap<>();
            toxiproxyClient.getProxies().forEach(proxy -> proxies.put(proxy.getName(), proxy));
            return proxies;
        } catch (IOException e) {
            log.error("Failed to list proxies", e);
            throw new RuntimeException("Failed to list proxies", e);
        }
    }

    private Proxy getProxy(String proxyName) {
        Proxy proxy = activeProxies.get(proxyName);
        if (proxy == null) {
            throw new IllegalStateException("Proxy not found: " + proxyName);
        }
        return proxy;
    }

    private void initializeProxies() {
        // Initialization logic if needed
    }
}