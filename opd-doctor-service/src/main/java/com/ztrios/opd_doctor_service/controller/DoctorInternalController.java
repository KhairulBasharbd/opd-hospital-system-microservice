package com.ztrios.opd_doctor_service.controller;


import com.ztrios.opd_doctor_service.service.DoctorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;


@RestController
@RequestMapping("/internal/doctors")
@RequiredArgsConstructor
public class DoctorInternalController {

    private final DoctorAvailabilityService availabilityService;

    @GetMapping("/{doctorId}/schedules/{scheduleId}/{serialNo}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable UUID doctorId,
            @PathVariable UUID scheduleId,
            @PathVariable Integer serialNo,
            @RequestParam @DateTimeFormat(pattern = "M/d/yy") LocalDate appointmentDate
    ) {
        boolean available = availabilityService.isScheduleAvailable(
                doctorId,
                scheduleId,
                serialNo,
                appointmentDate
        );
        return ResponseEntity.ok(available);
    }
}

