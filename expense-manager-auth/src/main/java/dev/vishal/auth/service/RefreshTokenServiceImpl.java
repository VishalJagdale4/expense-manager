package dev.vishal.auth.service;

import dev.common.exceptionutils.exceptions.UnauthorizedException;
import dev.vishal.auth.entity.RefreshTokens;
import dev.vishal.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-validity-ms}")
    private long refreshExpirationMs;

    public void createRefreshToken(UUID userId, String token) {

        RefreshTokens refreshToken = new RefreshTokens();
        refreshToken.setUserId(userId);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs));
        refreshToken.setCreatedOn(LocalDateTime.now());

        repository.save(refreshToken);
    }

    public RefreshTokens verifyRefreshToken(String token) {

        RefreshTokens refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            repository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        return refreshToken;
    }
}