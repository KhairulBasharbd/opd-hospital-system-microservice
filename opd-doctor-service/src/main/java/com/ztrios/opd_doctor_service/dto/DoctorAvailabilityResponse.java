package com.ztrios.opd_doctor_service.dto;

import com.ztrios.opd_doctor_service.enums.DoctorStatus;
import com.ztrios.opd_doctor_service.enums.Specialization;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DoctorAvailabilityResponse(UUID userId,
                                         String degree,
                                         Specialization specialization,
                                         Integer experienceYears,
                                         String licenseNumber,
                                         BigDecimal consultationFee,
                                         DoctorStatus status,
                                         String bio
//                                         List<DoctorScheduleResponse> schedules
) {}
