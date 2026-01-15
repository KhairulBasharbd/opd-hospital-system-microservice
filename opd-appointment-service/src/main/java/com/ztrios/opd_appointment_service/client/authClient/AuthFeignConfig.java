package com.ztrios.opd_appointment_service.client.authClient;


import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthFeignConfig {

    @Bean
    public ErrorDecoder authErrorDecoder() {
        return new AuthFeignErrorDecoder();
    }
}
