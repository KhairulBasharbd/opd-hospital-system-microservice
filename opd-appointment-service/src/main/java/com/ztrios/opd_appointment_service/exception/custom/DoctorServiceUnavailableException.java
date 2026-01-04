package com.ztrios.opd_appointment_service.exception.custom;

public class DoctorServiceUnavailableException extends RuntimeException {
    public DoctorServiceUnavailableException(String message) {
        super(message);
    }
}
