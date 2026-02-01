package dev.vishal.auth.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.exceptionutils.exceptions.UnauthorizedException;
import dev.vishal.auth.dto.AuthResponse;
import dev.vishal.auth.dto.LoginRequest;
import dev.vishal.auth.dto.RegisterRequest;
import dev.vishal.auth.dto.TokenValidationResponse;
import dev.vishal.auth.entity.AuthUser;
import dev.vishal.auth.entity.RefreshToken;
import dev.vishal.auth.entity.UserSession;
import dev.vishal.auth.repository.AuditLogRepository;
import dev.vishal.auth.repository.AuthUserRepository;
import dev.vishal.auth.repository.RefreshTokenRepository;
import dev.vishal.auth.repository.UserSessionRepository;
import dev.vishal.auth.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthUserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSessionRepository sessionRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditService auditService;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        log.info("Registering new user: {}", request.getUsername());

        // Validate username and email uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists", "USERNAME_EXISTS");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists", "EMAIL_EXISTS");
        }

        // Create user with BCrypt password hashing
        AuthUser user = AuthUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .roles(new HashSet<>(Set.of("USER"))) // Default role
                .isActive(true)
                .isLocked(false)
                .failedLoginAttempts(0)
                .passwordChangedAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build();

        user = userRepository.save(user);

        // Audit the registration
        auditService.logAction(user.getUserId(), user.getUsername(), "REGISTER", "/auth/register", "POST", 200, httpRequest);

        log.info("User registered successfully: {}", user.getUsername());

        // Generate tokens and create session
        return generateAuthResponse(user, httpRequest);
    }

    /**
     * Login user with username/email and password
     */
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("Login attempt for: {}", request.getUsernameOrEmail());

        // Find user
        AuthUser user = userRepository.findActiveUserByUsernameOrEmail(request.getUsernameOrEmail()).orElseThrow(() -> {
            auditService.logFailedLogin(request.getUsernameOrEmail(), httpRequest, "User not found or inactive");
            return new UnauthorizedException("Invalid credentials", "INVALID_CREDENTIALS");
        });

        // Check if account is locked
        if (user.getIsLocked()) {
            auditService.logFailedLogin(user.getUsername(), httpRequest, "Account locked");
            throw new UnauthorizedException("Account is locked due to multiple failed login attempts", "ACCOUNT_LOCKED");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.incrementFailedAttempts();
            userRepository.save(user);

            auditService.logFailedLogin(user.getUsername(), httpRequest, "Invalid password");

            if (user.getIsLocked()) {
                throw new UnauthorizedException("Account locked due to multiple failed attempts", "ACCOUNT_LOCKED");
            }

            throw new UnauthorizedException("Invalid credentials", "INVALID_CREDENTIALS");
        }

        // Successful login
        user.recordSuccessfulLogin();
        userRepository.save(user);

        // Audit successful login
        auditService.logAction(user.getUserId(), user.getUsername(), "LOGIN", "/auth/login", "POST", 200, httpRequest);

        log.info("User logged in successfully: {}", user.getUsername());

        // Generate tokens and create session
        return generateAuthResponse(user, httpRequest);
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr, HttpServletRequest httpRequest) {
        log.info("Refreshing access token");

        // Validate refresh token format
        if (!jwtTokenProvider.validateToken(refreshTokenStr)) {
            throw new UnauthorizedException("Invalid refresh token", "INVALID_REFRESH_TOKEN");
        }

        // Check token type
        String tokenType = jwtTokenProvider.getTokenType(refreshTokenStr);
        if (!"refresh".equals(tokenType)) {
            throw new UnauthorizedException("Token is not a refresh token", "INVALID_TOKEN_TYPE");
        }

        // Find refresh token in database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr).orElseThrow(() -> new UnauthorizedException("Refresh token not found", "TOKEN_NOT_FOUND"));

        // Validate refresh token
        if (!refreshToken.isValid()) {
            throw new UnauthorizedException("Refresh token is expired or revoked", "INVALID_REFRESH_TOKEN");
        }

        // Get user
        AuthUser user = userRepository.findById(refreshToken.getUserId()).orElseThrow(() -> new UnauthorizedException("User not found", "USER_NOT_FOUND"));

        // Check if user is active
        if (!user.getIsActive() || user.getIsLocked()) {
            throw new UnauthorizedException("User account is not active", "ACCOUNT_INACTIVE");
        }

        // Generate new access token (keep same refresh token)
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getUsername(), user.getEmail(), user.getRoles());

        LocalDateTime expiresAt = jwtTokenProvider.getExpirationFromToken(newAccessToken);

        // Update session in Redis
        createUserSession(newAccessToken, user, expiresAt, httpRequest);

        // Audit token refresh
        auditService.logAction(user.getUserId(), user.getUsername(), "TOKEN_REFRESH", "/auth/refresh", "POST", 200, httpRequest);

        return AuthResponse.builder().accessToken(newAccessToken).refreshToken(refreshTokenStr) // Keep same refresh token
                .tokenType("Bearer").expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds()).expiresAt(expiresAt).user(buildUserInfo(user)).build();
    }

    /**
     * Validate JWT token and return user info
     */
    public TokenValidationResponse validateToken(String token) {
        try {
            // First check JWT validity
            if (!jwtTokenProvider.validateToken(token)) {
                return TokenValidationResponse.invalid("Token is invalid or expired");
            }

            // Check if session exists in Redis (fast lookup)
            return sessionRepository.findByToken(token).map(session -> {
                if (!session.isValid()) {
                    return TokenValidationResponse.invalid("Session expired");
                }
                return TokenValidationResponse.valid(session.getUserId(), session.getUsername(), session.getEmail(), session.getRoles(), session.getExpiresAt());
            }).orElseGet(() -> {
                // Fallback to JWT parsing if session not in Redis
                try {
                    UUID userId = jwtTokenProvider.getUserIdFromToken(token);
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    Set<String> roles = jwtTokenProvider.getRolesFromToken(token);
                    LocalDateTime expiresAt = jwtTokenProvider.getExpirationFromToken(token);

                    return TokenValidationResponse.valid(userId, username, email, roles, expiresAt);
                } catch (Exception e) {
                    log.error("Failed to parse token: {}", e.getMessage());
                    return TokenValidationResponse.invalid("Failed to parse token");
                }
            });

        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return TokenValidationResponse.invalid("Token validation failed: " + e.getMessage());
        }
    }

    /**
     * Logout user and revoke tokens
     */
    @Transactional
    public void logout(String accessToken, HttpServletRequest httpRequest) {
        try {
            UUID userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            String username = jwtTokenProvider.getUsernameFromToken(accessToken);

            // Remove session from Redis
            sessionRepository.deleteByToken(accessToken);

            // Audit logout
            auditService.logAction(userId, username, "LOGOUT", "/auth/logout", "POST", 200, httpRequest);

            log.info("User logged out: {}", username);
        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
        }
    }

    /**
     * Revoke all refresh tokens for a user (logout from all devices)
     */
    @Transactional
    public void logoutAllDevices(UUID userId, HttpServletRequest httpRequest) {
        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllTokensByUserId(userId);

        // Clear all sessions from Redis
        sessionRepository.deleteByUserId(userId);

        AuthUser user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            auditService.logAction(userId, user.getUsername(), "LOGOUT_ALL_DEVICES", "/auth/logout-all", "POST", 200, httpRequest);
        }

        log.info("All sessions revoked for user: {}", userId);
    }

    /**
     * Generate complete auth response with tokens and session
     */
    private AuthResponse generateAuthResponse(AuthUser user, HttpServletRequest httpRequest) {
        // Generate access token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getUsername(), user.getEmail(), user.getRoles());

        // Generate refresh token
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getUsername());

        LocalDateTime accessTokenExpiry = jwtTokenProvider.getExpirationFromToken(accessToken);
        LocalDateTime refreshTokenExpiry = jwtTokenProvider.getExpirationFromToken(refreshToken);

        // Save refresh token to database
        RefreshToken refreshTokenEntity = RefreshToken.builder().token(refreshToken).userId(user.getUserId()).expiresAt(refreshTokenExpiry).isRevoked(false).deviceInfo(getDeviceInfo(httpRequest)).ipAddress(getClientIp(httpRequest)).build();
        refreshTokenRepository.save(refreshTokenEntity);

        // Create session in Redis
        createUserSession(accessToken, user, accessTokenExpiry, httpRequest);

        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds()).expiresAt(accessTokenExpiry).user(buildUserInfo(user)).build();
    }

    /**
     * Create user session in Redis
     */
    private void createUserSession(String token, AuthUser user, LocalDateTime expiresAt, HttpServletRequest request) {
        UserSession session = UserSession.builder().token(token).userId(user.getUserId()).username(user.getUsername()).email(user.getEmail()).firstName(user.getFirstName()).lastName(user.getLastName()).roles(user.getRoles()).loginTime(LocalDateTime.now()).ipAddress(getClientIp(request)).deviceInfo(getDeviceInfo(request)).expiresAt(expiresAt).build();

        sessionRepository.save(session);
    }

    /**
     * Build user info for response
     */
    private AuthResponse.UserInfo buildUserInfo(AuthUser user) {
        return AuthResponse.UserInfo.builder().userId(user.getUserId()).username(user.getUsername()).email(user.getEmail()).firstName(user.getFirstName()).lastName(user.getLastName()).roles(user.getRoles()).build();
    }

    /**
     * Get client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip.split(",")[0] : request.getRemoteAddr();
    }

    /**
     * Get device info from request
     */
    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 255)) : "Unknown";
    }
}