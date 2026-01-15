package com.ztrios.opd_billing_service.client.appointmentClient;

import com.ztrios.opd_billing_service.client.authClient.AuthFeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppointmentFeignConfig {

    @Bean
    public ErrorDecoder appointmentErrorDecoder() {
        return new AuthFeignErrorDecoder();
    }
}
