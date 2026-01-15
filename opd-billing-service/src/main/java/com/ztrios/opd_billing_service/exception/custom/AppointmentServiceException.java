package com.ztrios.opd_billing_service.exception.custom;


import org.springframework.http.HttpStatus;

public class AppointmentServiceException extends RemoteServiceException {
    public AppointmentServiceException(String message, HttpStatus status) {
        super(message, status);
    }
}
