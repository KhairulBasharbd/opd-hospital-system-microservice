package com.ztrios.opd_appointment_service.controller;


import com.ztrios.opd_appointment_service.dto.AppointmentResponse;
import com.ztrios.opd_appointment_service.dto.BookAppointmentRequest;
import com.ztrios.opd_appointment_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
@Slf4j
public class AppointmentController {

    private final AppointmentService service;


    @PostMapping("/create")
    public ResponseEntity<AppointmentResponse> book(
            @RequestHeader("X-User-Id") UUID patientId,
            @RequestHeader("X-User-Role") String role,
            @RequestBody BookAppointmentRequest request) {


        log.info("In controller ID {}, Role {} and DoctorId {}, Date {}", patientId, role, request.doctorId(), request.date());

        return ResponseEntity.ok(
                service.bookAppointment(patientId, request)
        );
    }
}
