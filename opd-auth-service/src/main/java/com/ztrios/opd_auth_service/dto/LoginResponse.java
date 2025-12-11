package com.ztrios.opd_auth_service.dto;


public record LoginResponse(String accessToken,
                            String tokenType,
                            long expiresInSeconds,
                            String email) {}


