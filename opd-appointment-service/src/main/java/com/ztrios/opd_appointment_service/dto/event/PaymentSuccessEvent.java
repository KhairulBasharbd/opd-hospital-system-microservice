package com.ztrios.opd_appointment_service.dto.event;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID appointmentId,
        UUID invoiceId,
        BigDecimal amountPaid,
        Instant paidAt
) {}
