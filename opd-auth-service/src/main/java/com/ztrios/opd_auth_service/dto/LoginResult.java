package com.ztrios.opd_auth_service.dto;

public record LoginResult(String accessToken, long expiresInSeconds, String email) {}
