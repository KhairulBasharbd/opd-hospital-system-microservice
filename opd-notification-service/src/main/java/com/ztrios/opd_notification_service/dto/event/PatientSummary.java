package com.ztrios.opd_notification_service.dto.event;


import java.util.UUID;

public record PatientSummary(
        UUID id,
        String email,
        String phone,
        String fullName
) {}
