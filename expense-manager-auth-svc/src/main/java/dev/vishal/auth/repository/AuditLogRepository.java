package dev.vishal.auth.repository;

import dev.vishal.auth.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);

    List<AuditLog> findByUserIdAndAction(UUID userId, String action);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.createdAt BETWEEN :startDate AND :endDate")
    List<AuditLog> findByUserIdAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.action = :action AND a.createdAt > :since")
    List<AuditLog> findRecentActionLogs(String action, LocalDateTime since);
}