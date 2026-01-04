package com.ztrios.opd_appointment_service.exception.custom;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String message) {
        super(message);
    }
}
