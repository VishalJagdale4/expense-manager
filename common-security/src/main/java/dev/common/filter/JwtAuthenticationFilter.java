package dev.common.filter;

import dev.common.model.AuthenticatedUser;
import dev.common.properties.SecurityProperties;
import dev.common.service.JwtValidationService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidationService jwtService;
    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Skips JWT validation for permitted URIs
        if (securityProperties.getPermitAll()
                .stream()
                .anyMatch(uri::endsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {

            Claims claims = jwtService.validateToken(token);

            AuthenticatedUser user = new AuthenticatedUser(
                    UUID.fromString(claims.get("userId", String.class)),
                    claims.getSubject()
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            logger.error("Error while validating request:", ex);
        }

        filterChain.doFilter(request, response);
    }
}