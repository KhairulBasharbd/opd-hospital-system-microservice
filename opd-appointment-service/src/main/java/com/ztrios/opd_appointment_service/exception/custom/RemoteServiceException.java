package com.ztrios.opd_appointment_service.exception.custom;


import org.springframework.http.HttpStatus;

public abstract class RemoteServiceException extends RuntimeException {

    private final HttpStatus status;

    protected RemoteServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}