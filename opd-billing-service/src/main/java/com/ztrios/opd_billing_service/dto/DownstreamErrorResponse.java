package com.ztrios.opd_billing_service.dto;


import java.time.Instant;

public record DownstreamErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message
) {}
