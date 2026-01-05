package com.ztrios.opd_billing_service.entity;


import com.ztrios.opd_billing_service.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {

    private UUID id;

    private PaymentMethod paymentMethod;

    private String paymentReference; // e.g., transaction ID from Stripe/PayPal
    private BigDecimal amount;
    private Instant paidAt;
}