package dev.vishal.auth.service;

import dev.vishal.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Clean up expired refresh tokens every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired refresh tokens");

        try {
            refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            log.info("Expired refresh tokens cleanup completed");
        } catch (Exception e) {
            log.error("Error during token cleanup: {}", e.getMessage(), e);
        }
    }
}