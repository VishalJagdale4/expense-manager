package dev.vishal.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Redis cache entity for storing user session data
 * TTL is managed at application level (matches JWT expiration)
 */
@RedisHash(value = "UserSession", timeToLive = 3600) // 1 hour default TTL
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String token; // JWT token as key

    @Indexed
    private UUID userId;

    @Indexed
    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private Set<String> roles;

    private LocalDateTime loginTime;

    private String ipAddress;

    private String deviceInfo;

    private LocalDateTime expiresAt;

    // Helper method to check if session is valid
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiresAt);
    }
}