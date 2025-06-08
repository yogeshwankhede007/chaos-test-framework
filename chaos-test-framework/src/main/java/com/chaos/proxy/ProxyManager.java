package com.chaos.proxy;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class ProxyManager {
    
    private final ToxiproxyClient toxiproxyClient;
    private final Map<String, Proxy> proxies;

    public ProxyManager(String host, int port) {
        this.toxiproxyClient = new ToxiproxyClient(host, port);
        this.proxies = new HashMap<>();
    }

    public void reset(String proxyName) throws IOException {
        Proxy proxy = getProxy(proxyName);
        if (proxy != null) {
            proxy.toxics().getAll().forEach(toxic -> {
                try {
                    toxic.remove();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to remove toxic", e);
                }
            });
            log.info("Reset proxy: {}", proxyName);
        }
    }

    public void simulateChaos(String proxyName, ChaosType chaosType, Map<String, Object> params) {
        try {
            Proxy proxy = getProxy(proxyName);
            switch (chaosType) {
                case LATENCY:
                    addLatencyToxic(proxy, params);
                    break;
                case BANDWIDTH:
                    addBandwidthToxic(proxy, params);
                    break;
                case PACKET_LOSS:
                    addPacketLossToxic(proxy, params);
                    break;
                case TIMEOUT:
                    addTimeoutToxic(proxy, params);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported chaos type: " + chaosType);
            }
        } catch (IOException e) {
            log.error("Failed to simulate chaos: {}", e.getMessage());
            throw new RuntimeException("Chaos simulation failed", e);
        }
    }

    private void addLatencyToxic(Proxy proxy, Map<String, Object> params) throws IOException {
        Long latency = (Long) params.get("latency");
        Double jitter = (Double) params.getOrDefault("jitter", 0.0);
        proxy.toxics().latency("latency_toxic", ToxicDirection.DOWNSTREAM, latency)
             .setJitter(jitter.longValue());
    }

    private void addBandwidthToxic(Proxy proxy, Map<String, Object> params) throws IOException {
        Long rate = (Long) params.get("rate");
        proxy.toxics().bandwidth("bandwidth_toxic", ToxicDirection.DOWNSTREAM, rate);
    }

    private void addPacketLossToxic(Proxy proxy, Map<String, Object> params) throws IOException {
        Integer percentage = (Integer) params.get("percentage");
        long bytes = 100; // Default value for data limit
        proxy.toxics().limitData("packet_loss_toxic", ToxicDirection.DOWNSTREAM, bytes)
             .setToxicity(percentage.floatValue() / 100);
    }

    private void addTimeoutToxic(Proxy proxy, Map<String, Object> params) throws IOException {
        Long timeout = (Long) params.get("timeout");
        proxy.toxics().timeout("timeout_toxic", ToxicDirection.DOWNSTREAM, timeout);
    }

    private Proxy getProxy(String name) throws IOException {
        if (!proxies.containsKey(name)) {
            proxies.put(name, toxiproxyClient.getProxy(name));
        }
        return proxies.get(name);
    }
}