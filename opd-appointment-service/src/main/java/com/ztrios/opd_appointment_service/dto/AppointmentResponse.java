package com.ztrios.opd_appointment_service.dto;

import com.ztrios.opd_appointment_service.enums.AppointmentStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AppointmentResponse(UUID appointmentId,

                                  UUID doctorId,
                                  UUID scheduleId,
                                  UUID patientUserId,
                                  LocalDate appointmentDate,
                                  Instant createdAt,

                                  AppointmentStatus status,
                                  String serialNo,
                                  String paymentLink ) {
}
