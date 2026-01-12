package com.ztrios.opd_auth_service.controller;


import com.ztrios.opd_auth_service.dto.PatientProfileResponse;
import com.ztrios.opd_auth_service.service.PatientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalController {

    private final PatientProfileService profileService;

    @GetMapping("/profile/{patientId}")
    public ResponseEntity<PatientProfileResponse> getProfileById(@PathVariable UUID patientId) {

        String userId = patientId.toString();
        return ResponseEntity.ok(profileService.getPatientProfile(userId));
    }
}
