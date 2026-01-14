package com.ztrios.opd_api_gateway.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;


import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
@Slf4j
public class JwtHeaderEnrichmentFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.warn("❌ Gateway reached: " + exchange.getRequest().getURI());
        log.warn("❌ Gateway reached with header: " + exchange.getRequest().getHeaders());


        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {

                    Jwt jwt = auth.getToken();

                    log.warn("✅ In JwtHeaderEnrichmentFilter : email - {}", jwt.getClaimAsString("email") );

                    ServerHttpRequest mutated = exchange
                            .getRequest()
                            .mutate()
                            .headers(headers -> {
                                headers.remove("X-User-ID");
                                headers.remove("X-User-Email");
                                headers.remove("X-User-Role");

                                headers.add("X-User-ID", jwt.getSubject());
                                headers.add("X-User-Email", jwt.getClaimAsString("email"));
                                headers.add("X-User-Role", jwt.getClaimAsString("role"));
                            })
                            .build();

                    log.warn("✅ Mutated request URI In JwtHeaderEnrichmentFilter : " + mutated.getURI());
                    log.warn("✅ Mutated request Header In JwtHeaderEnrichmentFilter : " + mutated.getHeaders());



                    return chain.filter(exchange.mutate().request(mutated).build());
                })

                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

