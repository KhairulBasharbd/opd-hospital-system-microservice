package com.ztrios.opd_auth_service.controller;


import com.ztrios.opd_auth_service.dto.PatientProfileResponse;
import com.ztrios.opd_auth_service.dto.PatientProfileUpdateRequest;
import com.ztrios.opd_auth_service.service.PatientProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class PatientProfileController {

    private final PatientProfileService profileService;

    // VIEW PROFILE
    @GetMapping
    public ResponseEntity<PatientProfileResponse> getProfile(Authentication authentication) {
        String userId = authentication.getName(); // userId from JWT
        return ResponseEntity.ok(profileService.getPatientProfile(userId));
    }

    // UPDATE PROFILE
    @PutMapping
    public ResponseEntity<PatientProfileResponse> updateProfile(
            Authentication authentication,
            @RequestBody @Valid PatientProfileUpdateRequest request
    ) {
        String userId = authentication.getName();
        return ResponseEntity.ok(profileService.updatePatientProfile(userId, request));
    }
}
