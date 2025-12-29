package com.ztrios.opd_appointment_service.dto;

import java.util.UUID;

public record AppointmentCreatedEvent(UUID appointmentId, UUID patientId) {
}
