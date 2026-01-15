package com.ztrios.opd_billing_service.client.authClient;



import com.ztrios.opd_billing_service.client.BaseFeignErrorDecoder;
import com.ztrios.opd_billing_service.exception.custom.AuthServiceException;
import org.springframework.http.HttpStatus;

public class AuthFeignErrorDecoder extends BaseFeignErrorDecoder {

    @Override
    protected RuntimeException mapToException(
            HttpStatus status,
            String message
    ) {
        return new AuthServiceException(message, status);
    }
}

