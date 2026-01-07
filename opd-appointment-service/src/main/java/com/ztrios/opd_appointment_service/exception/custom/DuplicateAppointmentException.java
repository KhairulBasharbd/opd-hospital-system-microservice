package com.ztrios.opd_appointment_service.exception.custom;

public class DuplicateAppointmentException extends RuntimeException {
    public DuplicateAppointmentException(String message) {
        super(message);
    }
}
