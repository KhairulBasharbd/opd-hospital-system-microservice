package com.ztrios.opd_billing_service.kafka;


import com.ztrios.opd_billing_service.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        kafkaTemplate.send("PAYMENT_SUCCESS", event.appointmentId().toString(), event);
    }
}
