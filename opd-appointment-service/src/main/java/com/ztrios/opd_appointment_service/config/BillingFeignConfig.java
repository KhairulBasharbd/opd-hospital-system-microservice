package com.ztrios.opd_appointment_service.config;


import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingFeignConfig {

    @Bean
    public ErrorDecoder billingErrorDecoder() {
        return new BillingFeignErrorDecoder();
    }
}
