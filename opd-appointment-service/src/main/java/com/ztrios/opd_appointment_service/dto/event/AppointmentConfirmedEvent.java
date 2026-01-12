package com.ztrios.opd_appointment_service.dto.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

//public record AppointmentConfirmedEvent(UUID appointmentId,
//                                        UUID doctorId,
//                                        UUID scheduleId,
//                                        LocalDate appointmentDate,
//                                        Integer serialNo) { }
public record AppointmentConfirmedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID appointmentId,
        LocalDate appointmentDate,
        UUID doctorId,
        String doctorName,
        BigDecimal consultationFee,
        UUID scheduleId,
        LocalTime startTime,
        LocalTime endTime,
        PatientSummary patient,
        Integer serialNo
) {}