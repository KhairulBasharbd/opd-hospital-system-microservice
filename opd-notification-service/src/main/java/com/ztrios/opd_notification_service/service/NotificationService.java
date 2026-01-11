package com.ztrios.opd_notification_service.service;

import com.ztrios.opd_notification_service.dto.NotificationRequest;

public interface NotificationService {
    void send(NotificationRequest request);
}
