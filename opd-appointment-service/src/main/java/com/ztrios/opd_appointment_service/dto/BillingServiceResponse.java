package com.ztrios.opd_appointment_service.dto;

import java.util.UUID;

public record BillingServiceResponse(UUID invoiceId,
                                     String paymentLink) {
}
