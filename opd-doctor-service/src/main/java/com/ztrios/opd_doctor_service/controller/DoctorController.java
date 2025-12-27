package com.ztrios.opd_doctor_service.controller;

import com.ztrios.opd_doctor_service.dto.*;
import com.ztrios.opd_doctor_service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/doctors")
@Slf4j
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // CRUD for Doctors (Admin only)
    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> createDoctor(       @RequestHeader("X-User-Id") String userId,
                                                              @RequestHeader("X-User-Role") String role,
                                                              @RequestBody CreateDoctorRequest request) {
        UUID createdBy = UUID.fromString(userId);

        log.info("🚀 X-User-Id : "+userId);
        log.info("🚀 X-User-Role : "+role);


        DoctorResponse response = doctorService.createDoctor(request, createdBy);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors(@RequestHeader("X-User-Id") String userId,
                                                              @RequestHeader("X-User-Role") String role) {

        UUID createdBy = UUID.fromString(userId);

        log.info("🚀 X-User-Id : "+userId);
        log.info("🚀 X-User-Role : "+role);

        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable UUID id, @RequestBody UpdateDoctorRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, request));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // Schedule Management (Admin or Doctor)
    @PostMapping("/{doctorId}/schedules")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<DoctorScheduleResponse> createSchedule(@PathVariable UUID doctorId, @RequestBody CreateDoctorScheduleRequest request) {
        return new ResponseEntity<>(doctorService.createSchedule(doctorId, request), HttpStatus.CREATED);
    }

    @GetMapping("/{doctorId}/schedules")
//    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')") // Patients might view availability
    public ResponseEntity<List<DoctorScheduleResponse>> getSchedulesByDoctorId(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorService.getSchedulesByDoctorId(doctorId));
    }

    @GetMapping("/schedules/{scheduleId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<DoctorScheduleResponse> getScheduleById(@PathVariable UUID scheduleId) {
        return ResponseEntity.ok(doctorService.getScheduleById(scheduleId));
    }

    @PutMapping("/schedules/{scheduleId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<DoctorScheduleResponse> updateSchedule(@PathVariable UUID scheduleId, @RequestBody CreateDoctorScheduleRequest request) {
        return ResponseEntity.ok(doctorService.updateSchedule(scheduleId, request));
    }

    @DeleteMapping("/schedules/{scheduleId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID scheduleId) {
        doctorService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    // Placeholder for current user ID
    private UUID getCurrentUserId() {
        // Implement based on auth
        return null;
    }
}