package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;
import dev.vishal.expensemanager.entity.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionService {

    Transaction createTransaction(TransactionDto dto);

    Transaction updateTransaction(TransactionDto dto);

    Transaction getTransaction(UUID id);

    List<TransactionResponseDto> getAllTransactions(TransactionDto dto);

    List<String> getTransactionNotes(TransactionDto dto);

    void deleteTransaction(UUID id);
}
