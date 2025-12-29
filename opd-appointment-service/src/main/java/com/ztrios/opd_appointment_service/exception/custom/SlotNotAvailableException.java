package com.ztrios.opd_appointment_service.exception.custom;

public class SlotNotAvailableException extends RuntimeException{
    public SlotNotAvailableException(String message){

        super(message);

    }
}
