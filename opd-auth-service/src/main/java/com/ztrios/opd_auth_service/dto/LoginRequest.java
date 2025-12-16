package com.ztrios.opd_auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email(message = "Please !! Provide Valid Email") String email,
        @NotBlank String password
) {}