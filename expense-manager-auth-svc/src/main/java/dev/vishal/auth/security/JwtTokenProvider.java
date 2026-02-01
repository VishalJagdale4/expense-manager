package dev.vishal.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-ms:3600000}") long accessTokenValidityMs,  // 1 hour
            @Value("${jwt.refresh-token-validity-ms:604800000}") long refreshTokenValidityMs  // 7 days
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
    }

    /**
     * Generate Access Token (short-lived)
     */
    public String generateAccessToken(UUID userId, String username, String email, Set<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("email", email)
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Generate Refresh Token (long-lived)
     */
    public String generateRefreshToken(UUID userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Parse and validate token
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Failed to parse token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get User ID from token
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Get Username from token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * Get Email from token
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    /**
     * Get Roles from token
     */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        return Set.copyOf((java.util.List<String>) claims.get("roles"));
    }

    /**
     * Get token expiration date
     */
    public LocalDateTime getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Get token type (access or refresh)
     */
    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !isTokenExpired(claims);
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * Get all claims from token
     */
    public Map<String, Object> getAllClaims(String token) {
        return parseToken(token);
    }

    /**
     * Get access token validity in seconds
     */
    public long getAccessTokenValidityInSeconds() {
        return accessTokenValidityMs / 1000;
    }

    /**
     * Get refresh token validity in seconds
     */
    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityMs / 1000;
    }
}