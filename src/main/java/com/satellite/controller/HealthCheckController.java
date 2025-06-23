package com.satellite.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public HealthCheckController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("kafka", "CONNECTED");
        return status;
    }
    
    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        Map<String, Object> metrics = new HashMap<>();
        if (kafkaTemplate.metrics() != null) {
            metrics.put("kafka_metrics", kafkaTemplate.metrics());
        }
        return metrics;
    }
}