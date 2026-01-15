package com.ztrios.opd_appointment_service.exception.custom;

import org.springframework.http.HttpStatus;

public class BillingServiceException extends RemoteServiceException {
    public BillingServiceException(String message, HttpStatus status) {
        super(message, status);
    }
}

