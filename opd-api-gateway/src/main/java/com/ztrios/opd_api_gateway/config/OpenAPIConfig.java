package com.ztrios.opd_api_gateway.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OPD Hospital System API")
                        .description("Centralized API Documentation for OPD Hospital System. " +
                                "Use the dropdown above to switch between different service APIs.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("OPD Hospital System Team")
                                .email("support@opd-hospital.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("OPD Hospital System GitHub Repository")
                        .url("https://github.com/Jewel-cse/opd-hospital-system"));
    }
}
