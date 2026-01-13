package com.ztrios.opd_billing_service.client;

import com.ztrios.opd_billing_service.dto.PatientProfileDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "opd-auth-service")
public interface AuthClient {

    @GetMapping("/internal/profile//{patientId}")
    PatientProfileDetails getPatientSummary(@PathVariable UUID patientId);
}