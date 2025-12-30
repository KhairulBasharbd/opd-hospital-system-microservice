package com.ztrios.opd_appointment_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


public record BookAppointmentRequest(UUID doctorId, UUID scheduleId, LocalDate date) {

}
