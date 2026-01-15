package com.ztrios.opd_appointment_service.dto;


import java.time.Instant;

public record DownstreamErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message
) {}
