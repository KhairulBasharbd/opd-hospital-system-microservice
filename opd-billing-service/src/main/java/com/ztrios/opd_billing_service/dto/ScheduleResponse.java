package com.ztrios.opd_billing_service.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record ScheduleResponse (
        UUID id,
        UUID doctorId,
        DayOfWeek daysOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        int maxPatients,
        int appointedPatients

){}