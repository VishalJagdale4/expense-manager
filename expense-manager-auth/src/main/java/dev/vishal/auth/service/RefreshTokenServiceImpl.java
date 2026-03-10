package dev.vishal.auth.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.helper.SecurityUtils;
import dev.vishal.auth.entity.RefreshTokens;
import dev.vishal.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));
        refreshToken.setCreatedOn(LocalDateTime.now());

        repository.save(refreshToken);
    }

    public RefreshTokens verifyRefreshToken(String token) {

        RefreshTokens refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            repository.delete(refreshToken);
            throw new BadRequestException("Refresh token expired");
        }

        return refreshToken;
    }

    @Transactional
    public void deleteByUser() {
        repository.deleteByUserId(SecurityUtils.getCurrentUser().getUserId());
    }
}