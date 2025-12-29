package com.ztrios.opd_appointment_service.dto;

public record BillingServiceResponse(String invoiceId,
                                     String paymentLink) {
}
