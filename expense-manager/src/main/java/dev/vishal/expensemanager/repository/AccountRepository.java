package dev.vishal.expensemanager.repository;

import dev.vishal.expensemanager.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByNameAndTypeAndIsDeletedFalse(String name, String type);

    List<Account> findByIdNotAndNameAndTypeAndIsDeletedFalse(Long id, String name, String type);

    List<Account> findByTypeAndIsDeletedFalseOrderByName(String type);

    List<Account> findAllByIsDeletedFalseOrderByName();
}
