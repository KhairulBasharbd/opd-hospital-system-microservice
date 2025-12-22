package com.ztrios.opd_auth_service.dto;

import com.ztrios.opd_auth_service.enums.Role;

public record UserCreationRequest(String username,
                                  String password,
                                  String email,
                                  Role role) {}

