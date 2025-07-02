package com.satellite.controller;

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthCheckControllerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void health_ShouldReturnHealthStatus() {
        HealthCheckController controller = new HealthCheckController(kafkaTemplate);

        Map<String, String> result = controller.health();

        assertNotNull(result);
        assertEquals("UP", result.get("status"));
        assertEquals("CONNECTED", result.get("kafka"));
        assertEquals(2, result.size());
    }

    @Test
    void metrics_WithKafkaMetrics_ShouldReturnMetrics() {
        Map<MetricName, Metric> mockMetrics = new HashMap<>();
        
        Mockito.doReturn(mockMetrics).when(kafkaTemplate).metrics();
        
        HealthCheckController controller = new HealthCheckController(kafkaTemplate);

        Map<String, Object> result = controller.metrics();

        assertNotNull(result);
        assertTrue(result.containsKey("kafka_metrics"));
        assertEquals(mockMetrics, result.get("kafka_metrics"));
    }

    @Test
    void metrics_WithNullKafkaMetrics_ShouldReturnEmptyMap() {
        when(kafkaTemplate.metrics()).thenReturn(null);
        
        HealthCheckController controller = new HealthCheckController(kafkaTemplate);

        Map<String, Object> result = controller.metrics();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}