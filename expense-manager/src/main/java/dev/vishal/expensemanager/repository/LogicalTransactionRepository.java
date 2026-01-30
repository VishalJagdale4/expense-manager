package dev.vishal.expensemanager.repository;

import dev.vishal.expensemanager.entity.LogicalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LogicalTransactionRepository extends JpaRepository<LogicalTransaction, UUID> {
}
