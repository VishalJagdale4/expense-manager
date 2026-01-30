package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.dao.TransactionDao;
import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;
import dev.vishal.expensemanager.entity.Account;
import dev.vishal.expensemanager.entity.Category;
import dev.vishal.expensemanager.entity.LogicalTransaction;
import dev.vishal.expensemanager.entity.Transaction;
import dev.vishal.expensemanager.repository.AccountRepository;
import dev.vishal.expensemanager.repository.CategoryRepository;
import dev.vishal.expensemanager.repository.LogicalTransactionRepository;
import dev.vishal.expensemanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final LogicalTransactionRepository logicalTransactionRepository;
    private final TransactionDao transactionDao;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Transaction createTransaction(TransactionDto dto) {

       accountRepository.findById(dto.getAccountId())
                .filter(acc -> !acc.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Account not found or deleted"));

       categoryRepository.findById(dto.getCategoryId())
                .filter(cat -> !cat.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Category not found or deleted"));

        // Gets +/- amount by txnType
        BigDecimal amount = applyTransactionTypeToAmount(dto.getTransactionType(), dto.getAmount());
        dto.setAmount(amount);

        Transaction transaction = new Transaction();
        copyDtoToEntity(dto, transaction);
        transaction.setVersionNumber(0L);
        transaction = transactionRepository.save(transaction);

        LogicalTransaction logicalTransaction = new LogicalTransaction();
        logicalTransaction.setTransactionId(transaction.getId());

        logicalTransaction = logicalTransactionRepository.save(logicalTransaction);

        transaction.setLogicalTransactionId(logicalTransaction.getId());
        transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public Transaction getTransaction(UUID id) {
        LogicalTransaction logicalTransaction = logicalTransactionRepository.findById(id)
                .filter(t -> !t.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        Transaction transaction = transactionRepository.findById(logicalTransaction.getTransactionId())
                .filter(txn -> !txn.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        return populateTransientFields(transaction);
    }

    @Override
    public List<TransactionResponseDto> getAllTransactions(TransactionDto dto) {
        return transactionDao.findTransactions(dto);
    }

    @Override
    public List<String> getTransactionNotes(TransactionDto dto) {
        return transactionDao.findNotes(dto);
    }

    @Override
    @Transactional
    public void deleteTransaction(UUID id) {
        LogicalTransaction logicalTransaction = logicalTransactionRepository.findById(id)
                .filter(txn -> !txn.getIsDeleted())
                .map(txn -> {
                    txn.setIsDeleted(true);
                    return txn;
                })
                .orElseThrow(() -> new BadRequestException("Transaction not found"));
        logicalTransactionRepository.save(logicalTransaction);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(TransactionDto dto) {
        LogicalTransaction logicalTransaction = logicalTransactionRepository.findById(dto.getId())
                .filter(txn -> !txn.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        Transaction existing = transactionRepository.findById(logicalTransaction.getTransactionId())
                .filter(txn -> !txn.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        boolean changed = isUserInputChanged(existing, dto);

        if (!changed) {
            logicalTransactionRepository.save(logicalTransaction);
            return transactionRepository.save(existing);
        }

        accountRepository.findById(dto.getAccountId())
                .filter(acc -> !acc.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Account not found"));

        categoryRepository.findById(dto.getCategoryId())
                .filter(cat -> !cat.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Category not found"));

        // Gets +/- amount by txnType
        BigDecimal amount = applyTransactionTypeToAmount(dto.getTransactionType(), dto.getAmount());
        dto.setAmount(amount);

        // clone transaction
        Transaction newTransaction = new Transaction();
        copyDtoToEntity(dto, newTransaction);
        newTransaction.setVersionNumber(existing.getVersionNumber() + 1);
        newTransaction.setLogicalTransactionId(existing.getId());

        Transaction saved = transactionRepository.save(newTransaction);

        // update new pointer txn
        logicalTransaction.setTransactionId(saved.getId());
        logicalTransactionRepository.save(logicalTransaction);

        return saved;
    }

    // ------------------ Helper methods ------------------

    private void copyDtoToEntity(TransactionDto dto, Transaction entity) {
        entity.setAmount(dto.getAmount());
        entity.setNote(dto.getNote());
        entity.setTransactionType(dto.getTransactionType());
        entity.setAccountId(dto.getAccountId());
        entity.setCategoryId(dto.getCategoryId());
        entity.setTransactionDatetime(dto.getTransactionDatetime());
    }

    private boolean isUserInputChanged(Transaction entity, TransactionDto dto) {
        if (notEquals(entity.getAmount(), dto.getAmount())) return true;
        if (notEquals(entity.getNote(), dto.getNote())) return true;
        if (notEquals(entity.getTransactionType(), dto.getTransactionType())) return true;
        if (notEquals(entity.getAccountId(), dto.getAccountId())) return true;
        if (notEquals(entity.getCategoryId(), dto.getCategoryId())) return true;
        return notEquals(entity.getTransactionDatetime(), dto.getTransactionDatetime());
    }

    private boolean notEquals(Object a, Object b) {
        if (a == null && b == null) return false;
        if (a == null || b == null) return true;

        if (a instanceof BigDecimal x && b instanceof BigDecimal y) {
            return x.compareTo(y) != 0;
        }

        if (a instanceof LocalDateTime x && b instanceof LocalDateTime y) {
            return !x.truncatedTo(ChronoUnit.MILLIS)
                    .equals(y.truncatedTo(ChronoUnit.MILLIS));
        }

        return !a.equals(b);
    }

    private Transaction populateTransientFields(Transaction transaction) {
        if (transaction.getAccountId() != null) {
            accountRepository.findById(transaction.getAccountId())
                    .ifPresent(account -> transaction.setAccountName(account.getName()));
        }

        if (transaction.getCategoryId() != null) {
            categoryRepository.findById(transaction.getCategoryId())
                    .ifPresent(category -> transaction.setCategoryName(category.getName()));
        }

        return transaction;
    }

    private BigDecimal applyTransactionTypeToAmount(String transactionType, BigDecimal amount) {
        return switch (transactionType) {
            case "CREDIT" -> amount.abs();
            case "DEBIT" -> amount.abs().negate();
            default -> throw new BadRequestException(transactionType + " is not allowed");
        };
    }

}



