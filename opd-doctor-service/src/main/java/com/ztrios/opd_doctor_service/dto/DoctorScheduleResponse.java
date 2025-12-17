package com.ztrios.opd_doctor_service.dto;

import com.ztrios.opd_doctor_service.enums.DaysOfWeek;

import java.time.LocalTime;
import java.util.UUID;

public record DoctorScheduleResponse (
    UUID id,
    UUID doctorId,
    DaysOfWeek daysOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    int maxPatients,
    int appointedPatients

){}
