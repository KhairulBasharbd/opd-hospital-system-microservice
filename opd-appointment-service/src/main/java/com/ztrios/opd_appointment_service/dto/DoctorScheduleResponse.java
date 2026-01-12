package com.ztrios.opd_appointment_service.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record DoctorScheduleResponse(
    UUID id,
    UUID doctorId,
    DayOfWeek daysOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    int maxPatients,
    int appointedPatients

){}
