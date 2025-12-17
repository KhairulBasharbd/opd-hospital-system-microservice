package com.ztrios.opd_doctor_service.dto;

import com.ztrios.opd_doctor_service.enums.DaysOfWeek;

import java.time.LocalTime;

public record CreateDoctorScheduleRequest (DaysOfWeek daysOfWeek,
                                           LocalTime startTime,
                                           LocalTime endTime,
                                           int maxPatients){
}
