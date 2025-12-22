package com.ztrios.opd_doctor_service.dto;


import com.ztrios.opd_doctor_service.enums.Role;

public record UserCreationRequest(String username, String password, String email, Role role) {}
