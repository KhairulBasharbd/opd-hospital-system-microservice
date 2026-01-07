package com.ztrios.opd_appointment_service.exception;

import com.ztrios.opd_appointment_service.exception.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(SlotNotAvailableException.class)
    public ResponseEntity<Object> handleConflict(SlotNotAvailableException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(AppointmentNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<Object> handleDoctorNotFound(DoctorNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DoctorServiceUnavailableException.class)
    public ResponseEntity<Object> handleDoctorServiceError(DoctorServiceUnavailableException ex) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(BillingServiceException.class)
    public ResponseEntity<Object> handleBillingService(BillingServiceException ex) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(PaymentCreationFailedException.class)
    public ResponseEntity<Object> handlePaymentFailedError(PaymentCreationFailedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DuplicateAppointmentException.class)
    public ResponseEntity<Object> handleDuplicateAppointment(DuplicateAppointmentException ex){
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }



//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleGeneric(Exception ex) {
//        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
//    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

}
