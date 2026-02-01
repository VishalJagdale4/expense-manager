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
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // seconds
    private LocalDateTime expiresAt;
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private UUID userId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Set<String> roles;
    }
}