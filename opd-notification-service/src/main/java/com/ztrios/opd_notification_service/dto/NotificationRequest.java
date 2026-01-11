package com.ztrios.opd_notification_service.dto;


import com.ztrios.opd_notification_service.enums.NotificationType;

public record NotificationRequest(
        NotificationType type,
        String email,
        String phone,
        String subject,
        String message
) {}
