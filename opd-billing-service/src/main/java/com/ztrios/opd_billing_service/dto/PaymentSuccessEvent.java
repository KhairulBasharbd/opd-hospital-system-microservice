package com.ztrios.opd_billing_service.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID appointmentId,
        UUID invoiceId,
        Instant paidAt
) {}
