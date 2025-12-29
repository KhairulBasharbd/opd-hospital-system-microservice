package com.ztrios.opd_appointment_service.service;

import com.ztrios.opd_appointment_service.dto.AppointmentConfirmedEvent;
import com.ztrios.opd_appointment_service.dto.AppointmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentEventProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void publishAppointmentCreated(AppointmentCreatedEvent event) {
        kafkaTemplate.send("APPOINTMENT_CREATED", event);
    }


    public void publishAppointmentConfirmed(AppointmentConfirmedEvent event) {
        kafkaTemplate.send("APPOINTMENT_CONFIRMED", event);
    }
}