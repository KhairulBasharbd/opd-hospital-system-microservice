package com.ztrios.opd_billing_service.client;

import com.ztrios.opd_billing_service.dto.DownstreamErrorResponse;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;


@Slf4j
public abstract class BaseFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        DownstreamErrorResponse error = extractError(response);

        HttpStatus status = HttpStatus.resolve(response.status());
        String message = error != null && error.message() != null
                ? error.message()
                : "Downstream service error";

        log.error("Feign error | method={} | status={} | message={}",
                methodKey, response.status(), message);

        return mapToException(status, message);
    }

    protected abstract RuntimeException mapToException(
            HttpStatus status,
            String message
    );

    private DownstreamErrorResponse extractError(Response response) {
        try {
            if (response.body() != null) {
                String body = Util.toString(
                        response.body().asReader(StandardCharsets.UTF_8)
                );
                return mapper.readValue(body, DownstreamErrorResponse.class);
            }
        } catch (Exception e) {
            log.warn("Failed to parse downstream error", e);
        }
        return null;
    }
}