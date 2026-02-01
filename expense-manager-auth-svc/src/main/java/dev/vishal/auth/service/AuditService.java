package dev.vishal.auth.service;

import dev.vishal.auth.entity.AuditLog;
import dev.vishal.auth.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log user action asynchronously
     */
    @Async
    public void logAction(
            UUID userId,
            String username,
            String action,
            String resource,
            String method,
            Integer statusCode,
            HttpServletRequest request
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .resource(resource)
                    .method(method)
                    .statusCode(statusCode)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .deviceInfo(getDeviceInfo(request))
                    .sessionId(request.getSession(false) != null ? request.getSession().getId() : null)
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created for action: {} by user: {}", action, username);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    /**
     * Log failed login attempt
     */
    @Async
    public void logFailedLogin(String usernameOrEmail, HttpServletRequest request, String reason) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .username(usernameOrEmail)
                    .action("LOGIN_FAILED")
                    .resource("/auth/login")
                    .method("POST")
                    .statusCode(401)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .errorMessage(reason)
                    .build();

            auditLogRepository.save(auditLog);
            log.warn("Failed login attempt for: {} from IP: {}", usernameOrEmail, getClientIp(request));
        } catch (Exception e) {
            log.error("Failed to log failed login: {}", e.getMessage());
        }
    }

    /**
     * Log API request with duration
     */
    @Async
    public void logApiRequest(
            UUID userId,
            String username,
            String resource,
            String method,
            Integer statusCode,
            Long durationMs,
            HttpServletRequest request,
            String responseMessage
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .action("API_CALL")
                    .resource(resource)
                    .method(method)
                    .statusCode(statusCode)
                    .durationMs(durationMs)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .responseMessage(responseMessage)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to log API request: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip.split(",")[0] : request.getRemoteAddr();
    }

    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 255)) : "Unknown";
    }
}