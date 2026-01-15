package com.ztrios.opd_billing_service.client.appointmentClient;


import com.ztrios.opd_billing_service.enums.AppointmentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@FeignClient(name = "opd-appointment-service")
public interface AppointmentClient {

    @GetMapping("/appointments/countAppointments")
    Integer countConfirmedAppointments(
            @RequestParam UUID doctorId,
            @RequestParam UUID scheduleId,
            @RequestParam LocalDate date,
            @RequestParam AppointmentStatus status
    );
}
