package dev.common.security;

import dev.common.feign.FeignCommonConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for Auth Service
 * Used to validate JWT tokens
 */
@FeignClient(
        name = "auth-service",
        url = "${auth.service.url:http://localhost:8102/auth-service}",
        configuration = FeignCommonConfig.class
)
public interface AuthServiceClient {

    @PostMapping("/auth/validate")
    JwtAuthenticationFilter.TokenValidationResponse validateToken(@RequestHeader("Authorization") String authHeader);
}