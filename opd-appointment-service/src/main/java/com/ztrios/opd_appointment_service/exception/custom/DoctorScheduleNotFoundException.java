package com.ztrios.opd_appointment_service.exception.custom;


import org.springframework.http.HttpStatus;

public class DoctorScheduleNotFoundException extends DoctorServiceException {
    public DoctorScheduleNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}