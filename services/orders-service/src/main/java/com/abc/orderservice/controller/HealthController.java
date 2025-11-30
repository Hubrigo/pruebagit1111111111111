package com.abc.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    private final Instant startTime = Instant.now();

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "service", "orders-service",
                "startedAt", startTime.toString(),
                "message", "Orders service running"
        );
    }
}
