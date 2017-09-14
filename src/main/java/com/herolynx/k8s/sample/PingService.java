package com.herolynx.k8s.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PingService {

    private final UUID id = UUID.randomUUID();
    @Autowired
    private ServiceConfig config;
    @Autowired
    private DbConfig dbConfig;
    private boolean healthy = true;
    private boolean ready = true;

    @GetMapping(path = "/hello")
    public String ping() {
        ready();
        health();
        return String.format("[%s] %s", id, config.getMessage());
    }

    @GetMapping(path = "/secrets")
    public String secrets() {
        ready();
        health();
        return String.format("[%s] Can I tell you a secret? Username: %s, password: %s", id, dbConfig.getUsername(), dbConfig.getPassword());
    }

    @GetMapping(path = "/probe/health")
    public boolean health() {
        return checkStatus(healthy, String.format("[%s] I'm sick!", id));
    }

    @PostMapping(path = "/probe/health")
    public String changeHealth() {
        healthy = !healthy;
        return String.format("%s is now: %s", id, healthy);
    }

    @GetMapping(path = "/probe/ready")
    public boolean ready() {
        return checkStatus(ready, String.format("[%s] Dude, I'm busy - leave me alone!", id));
    }

    @PostMapping(path = "/probe/ready")
    public String changeReady() {
        ready = !ready;
        return String.format("%s is now: %s", id, ready);
    }

    private boolean checkStatus(boolean s, String errMsg) {
        if (!s) {
            throw new RuntimeException(errMsg);
        }
        return s;
    }

}
