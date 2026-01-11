package com.ztrios.opd_notification_service.dto.event;


public record PatientSummary(
        String id,
        String email,
        String phone,
        String fullName
) {}
