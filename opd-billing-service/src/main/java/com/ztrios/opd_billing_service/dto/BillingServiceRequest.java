package com.ztrios.opd_billing_service.dto;


import java.util.UUID;

public record BillingServiceRequest(UUID appointmentId, UUID patientUserId, UUID doctorId
) {}
