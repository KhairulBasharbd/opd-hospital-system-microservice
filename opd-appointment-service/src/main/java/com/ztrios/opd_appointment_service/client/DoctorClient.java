package com.ztrios.opd_appointment_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@FeignClient(name = "opd-doctor-service")
public interface DoctorClient {

    @GetMapping("/internal/doctors/{doctorId}/schedules/{scheduleId}/{serialNo}/availability")
    boolean isScheduleAvailable(@PathVariable UUID doctorId,
                                @PathVariable UUID scheduleId,
                                @PathVariable  Integer lastSerialNo,
                                @RequestParam LocalDate date

    );

}
