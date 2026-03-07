package dev.vishal.auth.service;

import dev.vishal.auth.entity.RefreshTokens;

import java.util.UUID;

public interface RefreshTokenService {
    void createRefreshToken(UUID userId, String token);

    RefreshTokens verifyRefreshToken(String token);
}