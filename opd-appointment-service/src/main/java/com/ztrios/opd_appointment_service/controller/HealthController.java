package com.ztrios.opd_appointment_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for Appointment Service.
 * Provides endpoints for health monitoring and service information.
 */
@RestController
public class HealthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping({ "/health", "/actuator/health" })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", applicationName);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping({ "/info", "/actuator/info" })
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", applicationName);
        response.put("port", serverPort);
        response.put("description", "Appointment Management Service for OPD Hospital System");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}
