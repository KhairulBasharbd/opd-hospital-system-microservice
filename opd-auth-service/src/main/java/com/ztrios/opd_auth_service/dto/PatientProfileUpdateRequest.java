package com.ztrios.opd_auth_service.dto;

import com.ztrios.opd_auth_service.enums.BloodGroup;
import com.ztrios.opd_auth_service.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientProfileUpdateRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 100)
        String fullName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^(\\+?880)?1[3-9]\\d{8}$",
                message = "Invalid BD phone number format")
        String phone,

        LocalDate dateOfBirth,     // Optional
        Gender gender,          // Optional
        BloodGroup bloodGroup,      // Optional
        String address          // Optional
) {}