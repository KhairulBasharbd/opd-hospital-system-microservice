package com.ztrios.opd_billing_service.exception.custom;


import org.springframework.http.HttpStatus;

public class DoctorServiceException extends RemoteServiceException {

    public DoctorServiceException(String message, HttpStatus status) {
        super(message, status);
    }
}