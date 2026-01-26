package dev.vishal.expensemanager.dao;

import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;

import java.util.List;

public interface TransactionDao {
    List<TransactionResponseDto> findTransactions(TransactionDto transactionDto);
}
