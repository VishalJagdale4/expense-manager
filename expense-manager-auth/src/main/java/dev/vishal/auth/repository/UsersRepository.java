package dev.vishal.auth.repository;

import dev.vishal.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
    boolean existsByEmailIgnoreCaseAndIsDeletedFalse(String mailId);
    Optional<Users> findByEmailIgnoreCaseAndIsDeletedFalse(String mailId);
}
