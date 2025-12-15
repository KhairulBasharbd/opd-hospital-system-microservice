package com.ztrios.opd_auth_service.config;

import com.ztrios.opd_auth_service.security.oauth2.CustomOAuth2UserService;
import com.ztrios.opd_auth_service.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity

public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomOAuth2UserService oAuth2UserService, OAuth2AuthenticationSuccessHandler successHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2UserService = oAuth2UserService;
        this.successHandler = successHandler;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http.csrf(csrf -> csrf.disable());
//
//        http.sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers("/register/*", "/login", "/info", "/health", "/actuator/**")
//                .permitAll()
//
//                .anyRequest().authenticated()
//        );
//
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register/**",
                                "/oauth2/**",
                                "/actuator/**")
                        .permitAll()
                        .anyRequest().authenticated())

                // OAuth2
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u ->
                                u.userService(oAuth2UserService))
                        .successHandler(successHandler))

                // JWT
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
