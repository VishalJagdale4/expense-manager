package dev.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter for BFF and other services
 * Validates JWT token by calling Auth Service
 */
@Component
@Slf4j
@RequiredArgsConstructor
@EnableFeignClients
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper;

    @Value("${auth.service.enabled:true}")
    private boolean authServiceEnabled;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Skip authentication for public endpoints
        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip if auth service is disabled (for testing)
        if (!authServiceEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token from header
            String token = extractTokenFromRequest(request);

            if (token == null) {
                sendUnauthorizedResponse(response, "Missing authentication token", request.getRequestURI());
                return;
            }

            // Validate token with Auth Service
            TokenValidationResponse validation = authServiceClient.validateToken(token);

            if (!validation.getValid()) {
                sendUnauthorizedResponse(response, validation.getMessage(), request.getRequestURI());
                return;
            }

            // Set authentication in Security Context
            setAuthentication(validation, request);

            // Add user info to request attributes for audit logging
            request.setAttribute("userId", validation.getUserId());
            request.setAttribute("username", validation.getUsername());
            request.setAttribute("userEmail", validation.getEmail());
            request.setAttribute("userRoles", validation.getRoles());

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            sendUnauthorizedResponse(response, "Authentication failed", request.getRequestURI());
        }
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Set Spring Security authentication
     */
    private void setAuthentication(TokenValidationResponse validation, HttpServletRequest request) {
        List<SimpleGrantedAuthority> authorities = validation.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        validation.getUsername(),
                        null,
                        authorities
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Check if endpoint is public (doesn't require authentication)
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.contains("/login") ||
                uri.contains("/register") ||
                uri.contains("/health") ||
                uri.contains("/actuator") ||
                uri.contains("/swagger") ||
                uri.contains("/v3/api-docs");
    }

    /**
     * Send 401 Unauthorized response
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message, String path) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseDTO errorResponse = ResponseUtil.sendErrorResponse(
                message,
                "UNAUTHORIZED",
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED,
                path
        ).getBody();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * DTO for token validation response from Auth Service
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TokenValidationResponse {
        private Boolean valid;
        private java.util.UUID userId;
        private String username;
        private String email;
        private Set<String> roles;
        private LocalDateTime expiresAt;
        private String message;
    }
}