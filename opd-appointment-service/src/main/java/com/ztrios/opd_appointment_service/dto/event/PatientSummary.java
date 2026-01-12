package com.ztrios.opd_appointment_service.dto.event;


import java.util.UUID;

public record PatientSummary(
        UUID id,
        String email,
        String phone,
        String fullName
) {}