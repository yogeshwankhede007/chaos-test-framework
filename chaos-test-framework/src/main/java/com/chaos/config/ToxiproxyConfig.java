package com.chaos.config;

import io.github.kanro.moka.Toxiproxy;
import io.github.kanro.moka.Proxy;

public class ToxiproxyConfig {
    
    private Toxiproxy toxiproxy;

    public ToxiproxyConfig(String host, int port) {
        this.toxiproxy = new Toxiproxy(host, port);
    }

    public Proxy createProxy(String name, String upstream) {
        return toxiproxy.createProxy(name, upstream);
    }

    public void deleteProxy(String name) {
        toxiproxy.deleteProxy(name);
    }

    public void resetProxies() {
        toxiproxy.reset();
    }

    public void close() {
        toxiproxy.close();
    }
}