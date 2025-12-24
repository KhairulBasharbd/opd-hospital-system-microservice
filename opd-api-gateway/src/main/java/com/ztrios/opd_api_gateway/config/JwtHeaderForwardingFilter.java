package com.ztrios.opd_api_gateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtHeaderForwardingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication instanceof JwtAuthenticationToken jwtAuth){

            Jwt jwt = jwtAuth.getToken();

            String userId = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            String role = jwt.getClaimAsString("role");

            log.info("🚀 userId : " + userId);
            log.info("🚀 email : " + email);
            log.info("🚀 Role : " + role);



            MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

            mutableRequest.addHeader("X-User-Id",userId);
            mutableRequest.addHeader("X-User-Email",email);
            mutableRequest.addHeader("X-User-Role",role);

            filterChain.doFilter(mutableRequest,response);
            return;
        }
        filterChain.doFilter(request,response);


    }
}
