package com.ztrios.opd_auth_service.dto;

import com.ztrios.opd_auth_service.enums.BloodGroup;
import com.ztrios.opd_auth_service.enums.Gender;

import java.time.LocalDate;

public record PatientProfileResponse(
        String fullName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        Gender gender,
        BloodGroup bloodGroup,
        String address
) {}
