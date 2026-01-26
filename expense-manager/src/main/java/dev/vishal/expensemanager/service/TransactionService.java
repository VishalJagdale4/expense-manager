package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;
import dev.vishal.expensemanager.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Transaction createTransaction(TransactionDto dto);

    Transaction updateTransaction(TransactionDto dto);

    Transaction getTransaction(Long id);

    List<TransactionResponseDto> getAllTransactions(TransactionDto dto);

    List<String> getTransactionNotes(TransactionDto dto);

    void deleteTransaction(Long id);
}
