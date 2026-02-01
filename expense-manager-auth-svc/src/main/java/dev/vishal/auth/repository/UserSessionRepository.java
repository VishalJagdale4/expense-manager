package dev.vishal.auth.repository;

import dev.vishal.auth.entity.UserSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends CrudRepository<UserSession, String> {

    Optional<UserSession> findByToken(String token);

    List<UserSession> findByUserId(UUID userId);

    List<UserSession> findByUsername(String username);

    void deleteByUserId(UUID userId);

    void deleteByUsername(String username);

    void deleteByToken(String username);
}