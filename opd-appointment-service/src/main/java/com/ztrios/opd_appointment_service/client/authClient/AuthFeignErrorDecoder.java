package com.ztrios.opd_appointment_service.client.authClient;


import com.ztrios.opd_appointment_service.client.BaseFeignErrorDecoder;
import com.ztrios.opd_appointment_service.exception.custom.AuthServiceException;
import org.springframework.http.HttpStatus;

import java.nio.file.AccessDeniedException;

public class AuthFeignErrorDecoder extends BaseFeignErrorDecoder {

    @Override
    protected RuntimeException mapToException(
            HttpStatus status,
            String message
    ) {
        return new AuthServiceException(message, status);
    }
}

