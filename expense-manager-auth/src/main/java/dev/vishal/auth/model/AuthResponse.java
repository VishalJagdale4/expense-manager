package dev.vishal.auth.model;

import lombok.Builder;

@Builder
public record AuthResponse(String accessToken, String refreshToken) {
}