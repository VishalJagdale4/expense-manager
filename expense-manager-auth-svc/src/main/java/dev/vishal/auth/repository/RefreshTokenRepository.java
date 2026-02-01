package dev.vishal.auth.repository;

import dev.vishal.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserId(UUID userId);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.isRevoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findValidTokensByUserId(UUID userId, LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.userId = :userId")
    void revokeAllTokensByUserId(UUID userId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.token = :token")
    void revokeToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}