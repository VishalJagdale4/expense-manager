package dev.vishal.expensemanager.repository;

import dev.vishal.expensemanager.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByNameAndTypeAndDeletedFalse(String name, String type);
    List<BankAccount> findByIdNotAndNameAndTypeAndDeletedFalse(Long id, String name, String type);
    List<BankAccount> findByTypeAndDeletedFalseOrderByName(String type);
    List<BankAccount> findAllByDeletedFalseOrderByName();
}
