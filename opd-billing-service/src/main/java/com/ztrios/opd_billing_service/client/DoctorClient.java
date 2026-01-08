package com.ztrios.opd_billing_service.client;


import com.ztrios.opd_billing_service.dto.DoctorResponse;
import com.ztrios.opd_billing_service.dto.ScheduleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "opd-doctor-service")
public interface DoctorClient {
    @GetMapping("api/doctors/{id}")
    DoctorResponse getDoctorDetails(@PathVariable("id") UUID doctorId);

    @GetMapping("api/doctors/schedules/{scheduleId}")
    ScheduleResponse getScheduleDetails(@PathVariable UUID scheduleId);

}
