package com.ztrios.opd_appointment_service.client;


import com.ztrios.opd_appointment_service.config.DoctorFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@FeignClient(name = "opd-doctor-service", configuration = DoctorFeignConfig.class)
public interface DoctorClient {

    @GetMapping("/internal/doctors/{doctorId}/schedules/{scheduleId}/{lastSerialNo}/availability")
    boolean isScheduleAvailable(@PathVariable UUID doctorId,
                                @PathVariable UUID scheduleId,
                                @PathVariable  Integer lastSerialNo,
                                @RequestParam LocalDate appointmentDate

    );

}
