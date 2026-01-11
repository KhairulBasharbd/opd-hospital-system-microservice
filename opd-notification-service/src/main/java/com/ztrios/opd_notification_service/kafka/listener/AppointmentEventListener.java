package com.ztrios.opd_notification_service.kafka.listener;

import com.ztrios.opd_notification_service.dto.NotificationRequest;
import com.ztrios.opd_notification_service.dto.event.AppointmentConfirmedEvent;
import com.ztrios.opd_notification_service.dto.event.AppointmentCreatedEvent;
import com.ztrios.opd_notification_service.service.MessageFormatterService;
import com.ztrios.opd_notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventListener {

    private final MessageFormatterService formatterService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "APPOINTMENT_CREATED")
    public void onAppointmentCreated(AppointmentCreatedEvent event) {

        log.info("📩 Received appointment_created event: {}", event.eventId());

        NotificationRequest request = formatterService.formatAppointmentCreated(event);

        notificationService.send(request);
    }

    @KafkaListener(topics = "APPOINTMENT_CONFIRMED")
    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {

        log.info("📩 Received appointment_confirmed event: {}", event.eventId());

        NotificationRequest request = formatterService.formatAppointmentConfirmed(event);

        notificationService.send(request);
    }
}
