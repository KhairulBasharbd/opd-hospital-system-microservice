package com.ztrios.opd_appointment_service.kafka.listener;


import com.ztrios.opd_appointment_service.dto.PaymentSuccessEvent;
import com.ztrios.opd_appointment_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSuccessListener {


    private final AppointmentService service;


    @KafkaListener(topics = "PAYMENT_SUCCESS")
    public void handle(PaymentSuccessEvent event) {
        service.confirmAppointment(event.appointmentId());
    }
}