package com.ztrios.opd_appointment_service.exception.custom;

public class PaymentCreationFailedException extends RuntimeException {
    public PaymentCreationFailedException(String message) {
        super(message);
    }
}
