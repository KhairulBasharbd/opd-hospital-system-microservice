package com.ztrios.opd_doctor_service.config;

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

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI doctorServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Doctor Service API")
                        .description("Doctor and Schedule Management Service for OPD Hospital System. " +
                                "Provides doctor profiles, schedules, and availability management.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("OPD Hospital System Team")
                                .email("support@opd-hospital.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Direct Access"),
                        new Server().url("http://localhost:8080/doctors").description("Via API Gateway")));
    }
}
