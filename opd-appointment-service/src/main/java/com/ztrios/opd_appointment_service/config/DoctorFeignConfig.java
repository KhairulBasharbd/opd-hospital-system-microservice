package com.ztrios.opd_appointment_service.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DoctorFeignConfig {

    @Bean
    public ErrorDecoder doctorErrorDecoder() {
        return new DoctorFeignErrorDecoder();
    }
}

