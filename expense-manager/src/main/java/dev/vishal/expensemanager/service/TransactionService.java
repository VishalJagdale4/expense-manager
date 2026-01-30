package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;
import dev.vishal.expensemanager.entity.Transactions;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    Transactions createTransaction(TransactionDto dto);

    Transactions updateTransaction(TransactionDto dto);

    Transactions getTransaction(UUID id);

    List<TransactionResponseDto> getAllTransactions(TransactionDto dto);

    List<String> getTransactionNotes(TransactionDto dto);

    void deleteTransaction(UUID id);
}
