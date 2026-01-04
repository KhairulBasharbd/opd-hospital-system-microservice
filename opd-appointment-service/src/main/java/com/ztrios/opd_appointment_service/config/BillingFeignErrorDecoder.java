package com.ztrios.opd_appointment_service.config;


import com.ztrios.opd_appointment_service.exception.custom.BillingServiceException;
import com.ztrios.opd_appointment_service.exception.custom.PaymentCreationFailedException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BillingFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        log.error("Billing service Feign error. Method: {}, Status: {}",
                methodKey, response.status());

        return switch (response.status()) {

            case 400 -> new PaymentCreationFailedException(
                    "Invalid billing request"
            );

            case 404 -> new BillingServiceException(
                    "Billing resource not found"
            );

            case 500 -> new BillingServiceException(
                    "Billing Service internal error"
            );

            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
