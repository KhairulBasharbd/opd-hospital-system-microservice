package com.ztrios.opd_appointment_service.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID appointmentId,
        String invoiceId,
        BigDecimal amountPaid
) {}
