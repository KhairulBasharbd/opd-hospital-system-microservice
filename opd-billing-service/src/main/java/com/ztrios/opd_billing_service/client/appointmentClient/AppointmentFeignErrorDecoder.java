package com.ztrios.opd_billing_service.client.appointmentClient;


import com.ztrios.opd_billing_service.client.BaseFeignErrorDecoder;
import com.ztrios.opd_billing_service.exception.custom.AppointmentServiceException;
import com.ztrios.opd_billing_service.exception.custom.AuthServiceException;
import org.springframework.http.HttpStatus;

public class AppointmentFeignErrorDecoder extends BaseFeignErrorDecoder {

    @Override
    protected RuntimeException mapToException(
            HttpStatus status,
            String message
    ) {
        return new AppointmentServiceException(message, status);
    }
}

