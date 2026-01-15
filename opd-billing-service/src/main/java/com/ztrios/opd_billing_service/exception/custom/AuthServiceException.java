package com.ztrios.opd_billing_service.exception.custom;


import org.springframework.http.HttpStatus;

public class AuthServiceException extends RemoteServiceException {
    public AuthServiceException(String message, HttpStatus status) {
        super(message, status);
    }
}
