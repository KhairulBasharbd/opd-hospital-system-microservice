package com.ztrios.opd_billing_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


public record DoctorResponse(
        UUID userId,
        String degree,
        String specialization,
        Integer experienceYears,
        String licenseNumber,
        BigDecimal consultationFee,
        String status,
        String bio,
        UUID createdBy,
        Instant createdAt
) {}