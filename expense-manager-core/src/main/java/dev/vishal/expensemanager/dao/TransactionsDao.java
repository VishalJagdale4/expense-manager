package dev.vishal.expensemanager.dao;

import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;

import java.util.List;

public interface TransactionsDao {
    List<TransactionResponseDto> findTransactions(TransactionDto transactionDto);

    List<String> findNotes(TransactionDto transactionDto);
}
