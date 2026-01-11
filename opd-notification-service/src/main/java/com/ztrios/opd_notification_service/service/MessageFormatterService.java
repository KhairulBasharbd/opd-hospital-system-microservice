package com.ztrios.opd_notification_service.service;


import com.ztrios.opd_notification_service.dto.NotificationRequest;
import com.ztrios.opd_notification_service.dto.event.AppointmentConfirmedEvent;
import com.ztrios.opd_notification_service.dto.event.AppointmentCreatedEvent;

public interface MessageFormatterService {

    NotificationRequest formatAppointmentCreated(AppointmentCreatedEvent event);

    NotificationRequest formatAppointmentConfirmed(AppointmentConfirmedEvent event);
}
