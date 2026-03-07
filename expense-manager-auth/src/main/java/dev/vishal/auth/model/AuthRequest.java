package dev.vishal.auth.model;

import lombok.Builder;

@Builder
public record AuthRequest(String username, String password) {
}