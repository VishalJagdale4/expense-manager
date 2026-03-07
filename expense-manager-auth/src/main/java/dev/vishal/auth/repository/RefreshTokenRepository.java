package dev.vishal.auth.repository;

import dev.vishal.auth.entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, UUID> {

    Optional<RefreshTokens> findByToken(String token);

    void deleteByUserId(UUID userId);
}