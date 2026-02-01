package dev.vishal.auth.repository;

import dev.vishal.auth.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findByEmail(String email);

    @Query("SELECT u FROM AuthUser u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<AuthUser> findByUsernameOrEmail(String usernameOrEmail);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM AuthUser u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail) AND u.isActive = true AND u.isLocked = false")
    Optional<AuthUser> findActiveUserByUsernameOrEmail(String usernameOrEmail);
}