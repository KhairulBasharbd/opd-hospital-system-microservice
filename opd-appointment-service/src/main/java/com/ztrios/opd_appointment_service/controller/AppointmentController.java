package com.ztrios.opd_appointment_service.controller;


import com.ztrios.opd_appointment_service.dto.AppointmentResponse;
import com.ztrios.opd_appointment_service.dto.BookAppointmentRequest;
import com.ztrios.opd_appointment_service.enums.AppointmentStatus;
import com.ztrios.opd_appointment_service.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;


    @PostMapping("/create")
    public ResponseEntity<AppointmentResponse> book(
            @RequestHeader("X-User-Id") UUID patientId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody BookAppointmentRequest request) {


        log.info("In controller ID {}, Role {} and DoctorId {}, Date {}", patientId, role, request.doctorId(), request.date());

        return ResponseEntity.ok(
                appointmentService.bookAppointment(patientId, request)
        );
    }

    @GetMapping("/countAppointments")
    public Integer countConfirmedAppointments(
            @RequestParam UUID doctorId,
            @RequestParam UUID scheduleId,
            @RequestParam LocalDate date,
            @RequestParam AppointmentStatus status
    ) {
        log.info("In countConfirmedAppointments controller DoctorID {}, ScheduleId {}, Date {}, status {}",  doctorId, scheduleId, date, status);

        return appointmentService.countAppointments(doctorId, scheduleId, date, status);
    }


}
