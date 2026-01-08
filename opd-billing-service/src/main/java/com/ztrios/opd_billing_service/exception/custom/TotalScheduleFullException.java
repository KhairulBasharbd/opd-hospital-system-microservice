package com.ztrios.opd_billing_service.exception.custom;

public class TotalScheduleFullException extends RuntimeException {
    public TotalScheduleFullException(String message) {
        super(message);
    }
}
