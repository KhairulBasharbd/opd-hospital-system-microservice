package com.ztrios.opd_appointment_service.config;


import com.ztrios.opd_appointment_service.exception.custom.AuthServiceUnavailableException;
import com.ztrios.opd_appointment_service.exception.custom.UserNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        log.error(
                "Auth Service Feign error | method={} | status={}",
                methodKey,
                response.status()
        );

        return switch (response.status()) {

            case 404 -> new UserNotFoundException(
                    "User not found in Auth Service"
            );

            case 400 -> new AuthServiceUnavailableException(
                    "Invalid request sent to Auth Service"
            );

            case 401, 403 -> new AuthServiceUnavailableException(
                    "Unauthorized access to Auth Service"
            );

            case 503 -> new AuthServiceUnavailableException(
                    "Auth Service unavailable"
            );

            case 500 -> new AuthServiceUnavailableException(
                    "Auth Service internal server error"
            );

            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
