package dev.vishal.expensemanager.repository;

import dev.vishal.expensemanager.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
    boolean existsByEmailIgnoreCaseAndIsDeletedFalse(String mailId);
}
