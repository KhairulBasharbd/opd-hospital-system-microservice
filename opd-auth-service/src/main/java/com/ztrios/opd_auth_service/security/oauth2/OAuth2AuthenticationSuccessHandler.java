package com.ztrios.opd_auth_service.security.oauth2;


import com.ztrios.opd_auth_service.config.JwtUtil;
import com.ztrios.opd_auth_service.entity.UserEntity;
import com.ztrios.opd_auth_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OAuth2AuthenticationSuccessHandler(
            JwtUtil jwtUtil,
            UserRepository userRepository) {

        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow();

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().toString());

        response.setContentType("application/json");
        response.getWriter().write(
                "{\"token\":\"" + token + "\"}");
    }
}
