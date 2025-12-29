package com.ztrios.opd_appointment_service.dto;

import java.util.UUID;

public record BillingServiceRequest(UUID appointmentId,
                                    UUID patientId,
                                    UUID doctorId) {
}
