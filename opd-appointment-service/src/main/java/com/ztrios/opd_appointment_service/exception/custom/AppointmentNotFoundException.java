package com.ztrios.opd_appointment_service.exception.custom;

public class AppointmentNotFoundException extends RuntimeException{


    public AppointmentNotFoundException(String message){

        super(message);

    }
}
