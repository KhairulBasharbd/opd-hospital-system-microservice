package com.ztrios.opd_appointment_service.kafka.listener;


import com.ztrios.opd_appointment_service.dto.event.PaymentSuccessEvent;
import com.ztrios.opd_appointment_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSuccessListener {

    private final AppointmentService appointmentService;

    @KafkaListener(topics = "PAYMENT_SUCCESS")
    public void handle(PaymentSuccessEvent event, Acknowledgment ack) {

        log.info("📥 PAYMENT_SUCCESS received for appointmentId={}", event.appointmentId());

        try {
            appointmentService.confirmAppointment(event.appointmentId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("❌ Failed to process PAYMENT_SUCCESS", ex);
        }
    }
}
