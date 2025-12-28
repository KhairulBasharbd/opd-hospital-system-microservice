package com.ztrios.opd_appointment_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI appointmentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Appointment Service API")
                        .description("Patient Appointment Management Service for OPD Hospital System. " +
                                "Provides appointment scheduling, rescheduling, and cancellation.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("OPD Hospital System Team")
                                .email("support@opd-hospital.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Direct Access"),
                        new Server().url("http://localhost:8080/appointments").description("Via API Gateway")));
    }
}
