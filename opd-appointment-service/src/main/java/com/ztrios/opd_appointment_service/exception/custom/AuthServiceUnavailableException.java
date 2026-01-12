package com.ztrios.opd_appointment_service.exception.custom;

public class AuthServiceUnavailableException extends RuntimeException {
    public AuthServiceUnavailableException(String message) {
        super(message);
    }
}
