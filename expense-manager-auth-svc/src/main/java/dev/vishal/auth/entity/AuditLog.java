package dev.vishal.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_action", columnList = "action")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "action", nullable = false, length = 100)
    private String action; // LOGIN, LOGOUT, API_CALL, PASSWORD_CHANGE, etc.

    @Column(name = "resource", length = 255)
    private String resource; // API endpoint or resource accessed

    @Column(name = "method", length = 10)
    private String method; // GET, POST, PUT, DELETE

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_message", length = 500)
    private String responseMessage;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "duration_ms")
    private Long durationMs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "session_id", length = 100)
    private String sessionId;
}