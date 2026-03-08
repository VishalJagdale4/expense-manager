package dev.vishal.expensemanager.repository;

import dev.vishal.expensemanager.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserIdAndNameAndTypeAndIsDeletedFalse(UUID userId, String name, String type);

    List<Account> findByIdNotAndUserIdAndNameAndTypeAndIsDeletedFalse(Long id, UUID userId, String name, String type);

    List<Account> findByUserIdAndTypeAndIsDeletedFalseOrderByName(UUID userId, String type);

    List<Account> findAllByUserIdAndIsDeletedFalseOrderByName(UUID userId);
}
