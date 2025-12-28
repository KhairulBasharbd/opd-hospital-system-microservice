package com.ztrios.opd_auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

        @Bean
        public OpenAPI authServiceOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Auth Service API")
                                                .description("Authentication and Authorization Service for OPD Hospital System. "
                                                                +
                                                                "Provides user registration, login, and JWT token management.")
                                                .version("v1.0.0")
                                                .contact(new Contact()
                                                                .name("OPD Hospital System Team")
                                                                .email("support@opd-hospital.com"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
        }
}
