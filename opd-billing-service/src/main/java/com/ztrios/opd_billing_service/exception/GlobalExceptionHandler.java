package com.ztrios.opd_billing_service.exception;

import com.ztrios.opd_billing_service.exception.custom.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<?> handleInvoiceNotFound(InvoiceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PaymentAlreadyDoneException.class)
    public ResponseEntity<?> handlePaymentDone(PaymentAlreadyDoneException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TotalScheduleFullException.class)
    public ResponseEntity<?> handleAppointmentfullException(TotalScheduleFullException ex){

        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }


    @ExceptionHandler(DoctorScheduleNotFoundException.class)
    public ResponseEntity<Object> handleDoctorNotFound(DoctorScheduleNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }


//     * Handles all downstream (Feign) service errors
//     */
    @ExceptionHandler(RemoteServiceException.class)
    public ResponseEntity<Object> handleRemoteServiceException(RemoteServiceException ex) {

        log.error("Downstream error propagated: {}", ex.getMessage());

        return buildErrorResponse(ex.getStatus(), ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {

        log.error("An unexpected error occurred", ex);
        // 3. (Optional) If you are in development, you might want to return the actual message
        // return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }


    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

}
