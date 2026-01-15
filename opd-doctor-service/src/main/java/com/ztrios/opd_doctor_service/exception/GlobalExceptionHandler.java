package com.ztrios.opd_doctor_service.exception;

import com.ztrios.opd_doctor_service.exception.custom.DoctorNotFoundException;
import com.ztrios.opd_doctor_service.exception.custom.ScheduleNotFoundException;
import com.ztrios.opd_doctor_service.exception.custom.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;



@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleNotFound(UnauthorizedException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }


    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ScheduleNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(DoctorNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
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
