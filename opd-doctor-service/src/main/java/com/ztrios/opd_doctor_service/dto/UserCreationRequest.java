package com.ztrios.opd_doctor_service.dto;



public record UserCreationRequest(String username, String password, String email, String role) {}
