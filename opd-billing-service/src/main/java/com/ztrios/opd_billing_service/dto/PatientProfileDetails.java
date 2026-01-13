package com.ztrios.opd_billing_service.dto;


import java.time.LocalDate;

public record PatientProfileDetails(
        String fullName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String gender,
        String bloodGroup,
        String address
) {}
