package com.ztrios.opd_appointment_service.dto;

import java.time.LocalDate;
import java.util.UUID;

public record BillingServiceRequest(UUID appointmentId, UUID patientUserId, UUID doctorId, UUID scheduleId, LocalDate appointmentDate) {}
