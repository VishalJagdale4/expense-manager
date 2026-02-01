package dev.vishal.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {

    private Boolean valid;
    private UUID userId;
    private String username;
    private String email;
    private Set<String> roles;
    private LocalDateTime expiresAt;
    private String message;

    public static TokenValidationResponse invalid(String message) {
        return TokenValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
    }

    public static TokenValidationResponse valid(UUID userId, String username, String email,
                                                Set<String> roles, LocalDateTime expiresAt) {
        return TokenValidationResponse.builder()
                .valid(true)
                .userId(userId)
                .username(username)
                .email(email)
                .roles(roles)
                .expiresAt(expiresAt)
                .message("Token is valid")
                .build();
    }
}