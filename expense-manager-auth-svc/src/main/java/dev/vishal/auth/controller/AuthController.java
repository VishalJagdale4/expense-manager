package dev.vishal.auth.controller;

import dev.common.exceptionutils.exceptions.UnauthorizedException;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.auth.dto.AuthResponse;
import dev.vishal.auth.dto.LoginRequest;
import dev.vishal.auth.dto.RegisterRequest;
import dev.vishal.auth.dto.TokenValidationResponse;
import dev.vishal.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Register request for username: {}", request.getUsername());

        AuthResponse response = authService.register(request, httpRequest);

        return ResponseUtil.sendResponse(
                response,
                LocalDateTime.now(),
                HttpStatus.CREATED,
                httpRequest.getRequestURI()
        );
    }

    /**
     * Login user
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Login request for: {}", request.getUsernameOrEmail());

        AuthResponse response = authService.login(request, httpRequest);

        return ResponseUtil.sendResponse(
                response,
                LocalDateTime.now(),
                HttpStatus.OK,
                httpRequest.getRequestURI()
        );
    }

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO> refreshToken(
            @RequestHeader("Refresh-Token") String refreshToken,
            HttpServletRequest httpRequest
    ) {
        log.info("Token refresh request");

        AuthResponse response = authService.refreshToken(refreshToken, httpRequest);

        return ResponseUtil.sendResponse(
                response,
                LocalDateTime.now(),
                HttpStatus.OK,
                httpRequest.getRequestURI()
        );
    }

    /**
     * Validate JWT token (used by BFF and other services)
     */
    @PostMapping("/validate")
    public ResponseEntity<ResponseDTO> validateToken(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest
    ) {
        String token = extractTokenFromHeader(authHeader);

        TokenValidationResponse response = authService.validateToken(token);

        if (response.getValid()) {
            return ResponseUtil.sendResponse(
                    response,
                    LocalDateTime.now(),
                    HttpStatus.OK,
                    httpRequest.getRequestURI()
            );
        } else {
            throw new UnauthorizedException(response.getMessage());
        }
    }

    /**
     * Logout user
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest
    ) {
        String token = extractTokenFromHeader(authHeader);

        authService.logout(token, httpRequest);

        return ResponseUtil.sendResponse(
                "Logout successful",
                LocalDateTime.now(),
                HttpStatus.OK,
                httpRequest.getRequestURI()
        );
    }

    /**
     * Logout from all devices
     */
    @PostMapping("/logout-all")
    public ResponseEntity<ResponseDTO> logoutAllDevices(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest
    ) {
        String token = extractTokenFromHeader(authHeader);

        // Extract user ID from token (this will be done by auth service)
        TokenValidationResponse validation = authService.validateToken(token);

        if (!validation.getValid()) {
            return ResponseUtil.sendErrorResponse(
                    "Invalid token",
                    "INVALID_TOKEN",
                    LocalDateTime.now(),
                    HttpStatus.UNAUTHORIZED,
                    httpRequest.getRequestURI()
            );
        }

        authService.logoutAllDevices(validation.getUserId(), httpRequest);

        return ResponseUtil.sendResponse(
                "Logged out from all devices",
                LocalDateTime.now(),
                HttpStatus.OK,
                httpRequest.getRequestURI()
        );
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ResponseDTO> health(HttpServletRequest httpRequest) {
        return ResponseUtil.sendResponse(
                "Auth service is healthy",
                LocalDateTime.now(),
                HttpStatus.OK,
                httpRequest.getRequestURI()
        );
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header format");
    }
}