package com.ztrios.opd_appointment_service.kafka.producer;

import com.ztrios.opd_appointment_service.dto.event.AppointmentConfirmedEvent;
import com.ztrios.opd_appointment_service.dto.event.AppointmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void publishAppointmentCreated(AppointmentCreatedEvent event) {

        log.info("📤 APPOINTMENT_CREATED published: {}", event.appointmentId());

        kafkaTemplate.send("APPOINTMENT_CREATED",event.appointmentId().toString(), event);
    }


    public void publishAppointmentConfirmed(AppointmentConfirmedEvent event) {

        log.info("📤 APPOINTMENT_CONFIRMED published: {}", event.appointmentId());

        kafkaTemplate.send("APPOINTMENT_CONFIRMED", event.appointmentId().toString(), event);
    }
}