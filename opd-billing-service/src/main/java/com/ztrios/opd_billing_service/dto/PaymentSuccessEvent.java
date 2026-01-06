package com.ztrios.opd_billing_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID appointmentId,
        UUID invoiceId,
        BigDecimal amountPaid,
        Instant paidAt
) {}
