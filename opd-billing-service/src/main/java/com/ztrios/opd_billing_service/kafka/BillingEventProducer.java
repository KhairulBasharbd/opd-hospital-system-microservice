package com.ztrios.opd_billing_service.kafka;


import com.ztrios.opd_billing_service.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentSuccess(PaymentSuccessEvent event) {

        log.info("📤 PAYMENT_SUCCESS published: {}", event.appointmentId());

        kafkaTemplate.send("PAYMENT_SUCCESS", event.appointmentId().toString(), event);
    }
}
