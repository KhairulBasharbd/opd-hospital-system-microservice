package com.ztrios.opd_api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

        private static final String[] SWAGGER_WHITELIST = {
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/aggregate/**",
                        "/service/**"
        };

        private static final String[] PUBLIC_ENDPOINTS = {
                        "/auth/**",
                        "/actuator/**"
        };

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtUtil jwtUtil) {
                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeExchange(exchanges -> exchanges
                                                .pathMatchers(SWAGGER_WHITELIST).permitAll()
                                                .pathMatchers(PUBLIC_ENDPOINTS).permitAll()
                                                .anyExchange().authenticated())
                                // Configure OAuth2 Resource Server with custom JWT decoder
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtDecoder(jwtUtil.jwtDecoder())))
                                .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
