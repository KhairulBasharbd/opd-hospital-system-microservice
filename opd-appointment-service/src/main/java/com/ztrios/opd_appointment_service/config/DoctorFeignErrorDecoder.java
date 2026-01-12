package com.ztrios.opd_appointment_service.config;

import com.ztrios.opd_appointment_service.exception.custom.DoctorNotFoundException;
import com.ztrios.opd_appointment_service.exception.custom.DoctorServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



@Slf4j
public class DoctorFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        log.error("Doctor service Feign error. Method: {}, Status: {}",
                methodKey, response.status());

        return switch (response.status()) {

            case 404 -> new DoctorNotFoundException(
                    "Doctor or schedule not found in Doctor Service"
            );

            case 400 -> new DoctorServiceUnavailableException(
                    "Invalid request sent to Doctor Service"
            );

            case 503 -> new DoctorServiceUnavailableException(
                    "Doctor Service is unavailable"
            );

            case 500 -> new DoctorServiceUnavailableException(
                    "Doctor Service internal error"
            );

            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}

