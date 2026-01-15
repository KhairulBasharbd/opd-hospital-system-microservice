package com.ztrios.opd_appointment_service.client.billingClient;


import com.ztrios.opd_appointment_service.client.BaseFeignErrorDecoder;
import com.ztrios.opd_appointment_service.exception.custom.AuthServiceException;
import com.ztrios.opd_appointment_service.exception.custom.BillingServiceException;
import org.springframework.http.HttpStatus;

public class BillingFeignErrorDecoder extends BaseFeignErrorDecoder {

    @Override
    protected RuntimeException mapToException(
            HttpStatus status,
            String message
    ) {
        return new BillingServiceException(message, status);
    }
}

