package com.ztrios.opd_doctor_service.controller;

import com.ztrios.opd_doctor_service.dto.*;
import com.ztrios.opd_doctor_service.enums.*;

import com.ztrios.opd_doctor_service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/available")
    public ResponseEntity<List<DoctorAvailabilityResponse>> getAvailableDoctors(@RequestParam("date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
                                                                    @RequestParam("specialization") Specialization specialization){
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        log.info("Fetching doctors for date={} ({}) and specialization={}",
                date, dayOfWeek, specialization);

        return ResponseEntity.ok(
                doctorService.getAvailableDoctors(dayOfWeek, specialization)
        );

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


    // Placeholder for current user ID
    private UUID getCurrentUserId() {
        // Implement based on auth
        return null;
    }
}