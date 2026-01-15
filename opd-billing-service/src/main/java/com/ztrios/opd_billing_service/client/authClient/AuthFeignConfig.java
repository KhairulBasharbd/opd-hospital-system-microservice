package com.ztrios.opd_billing_service.client.authClient;


import com.ztrios.opd_billing_service.client.appointmentClient.AppointmentFeignErrorDecoder;
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
