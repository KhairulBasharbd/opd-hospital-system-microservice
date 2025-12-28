package com.ztrios.opd_doctor_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ztrios.opd_doctor_service.repository.*;
import com.ztrios.opd_doctor_service.dto.*;
import com.ztrios.opd_doctor_service.entity.*;
import com.ztrios.opd_doctor_service.exception.*;
import com.ztrios.opd_doctor_service.service.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors")

public class ScheduleController {

    private final ScheduleService scheduleService;


    // Schedule Management (Admin or Doctor)
    @PostMapping("/{doctorId}/schedules")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<DoctorScheduleResponse> createSchedule(@PathVariable UUID doctorId, @RequestBody CreateDoctorScheduleRequest request) {
        return new ResponseEntity<>(scheduleService.createSchedule(doctorId, request), HttpStatus.CREATED);
    }

    @GetMapping("/{doctorId}/schedules")
//    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')") // Patients might view availability
    public ResponseEntity<List<DoctorScheduleResponse>> getSchedulesByDoctorId(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDoctorId(doctorId));
    }

    @GetMapping("/schedules/{scheduleId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<DoctorScheduleResponse> getScheduleById(@PathVariable UUID scheduleId) {
        return ResponseEntity.ok(scheduleService.getScheduleById(scheduleId));
    }

    @PutMapping("/schedules/{scheduleId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<DoctorScheduleResponse> updateSchedule(@PathVariable UUID scheduleId, @RequestBody CreateDoctorScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(scheduleId, request));
    }

    @DeleteMapping("/schedules/{scheduleId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }



}
