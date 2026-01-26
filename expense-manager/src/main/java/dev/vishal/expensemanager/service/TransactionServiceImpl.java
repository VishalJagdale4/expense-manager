package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.dao.TransactionDao;
import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;
import dev.vishal.expensemanager.entity.BankAccount;
import dev.vishal.expensemanager.entity.Transaction;
import dev.vishal.expensemanager.repository.BankAccountRepository;
import dev.vishal.expensemanager.repository.CategoryRepository;
import dev.vishal.expensemanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDao transactionDao;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Transaction createTransaction(TransactionDto dto) {

        BankAccount bankAccount = bankAccountRepository.findById(dto.getAccountId())
                .filter(account -> !account.getDeleted())
                .orElseThrow(() -> new BadRequestException("Bank account not found"));

        BigDecimal amount =
                getTransactionAmount(dto.getTransactionType(), dto.getAmount(), false); // Gets +/- amount by txnType
        bankAccount.setBalance(bankAccount.getBalance().add(amount)); // Balance update
        bankAccountRepository.save(bankAccount);

        Transaction transaction = new Transaction();
        copyDtoToEntity(dto, transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .filter(t -> !t.getDeleted())
                .map(this::populateTransientFields)
                .orElseThrow(() -> new BadRequestException("Transaction not found"));
    }

    @Override
    public List<TransactionResponseDto> getAllTransactions(TransactionDto dto) {
        return transactionDao.findTransactions(dto);
    }

    @Override
    public List<String> getTransactionNotes(TransactionDto dto) {
        return transactionRepository.findByDeletedFalseAndNoteContainingIgnoreCase(dto.getNoteLike())
                .stream()
                .map(Transaction::getNote)
                .distinct()
                .toList();
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(txn -> !txn.getDeleted())
                .map(txn -> {
                    txn.setDeleted(true);
                    return txn;
                })
                .orElseThrow(() -> new BadRequestException("Transaction not found"));
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(TransactionDto dto) {
        Transaction existing = transactionRepository.findById(dto.getId())
                .filter(txn -> !txn.getDeleted())
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        boolean changed = isUserInputChanged(existing, dto);

        if (!changed) {
            return transactionRepository.save(existing);
        }

        BankAccount bankAccount = bankAccountRepository.findById(dto.getAccountId())
                .filter(account -> !account.getDeleted())
                .orElseThrow(() -> new BadRequestException("Bank account not found"));

        BigDecimal newAmount =
                getTransactionAmount(dto.getTransactionType(), dto.getAmount(), false);  // Gets +/- amount by txnType

        BigDecimal oldAmount = // Gets +/- amount by txnType and reverse it
                getTransactionAmount(existing.getTransactionType(), existing.getAmount(), true);
        bankAccount.setBalance(bankAccount.getBalance().add(oldAmount).add(newAmount)); // Balance update6
        bankAccountRepository.save(bankAccount);

        // clone transaction
        Transaction newTransaction = new Transaction();
        copyDtoToEntity(dto, newTransaction);
        newTransaction.setLastTransactionId(existing.getId());

        Transaction saved = transactionRepository.save(newTransaction);

        // mark old deleted
        existing.setDeleted(true);
        transactionRepository.save(existing);

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
        if (notEquals(entity.getTransactionDatetime(), dto.getTransactionDatetime())) return true;
        return false;
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

    private Transaction populateTransientFields(Transaction t) {
        if (t.getAccountId() != null) {
            bankAccountRepository.findById(t.getAccountId())
                    .ifPresent(b -> t.setAccountName(b.getName()));
        }
        if (t.getCategoryId() != null) {
            categoryRepository.findById(t.getCategoryId())
                    .ifPresent(c -> t.setCategoryName(c.getName()));
        }
        return t;
    }

    private BigDecimal getTransactionAmount(String transactionType, BigDecimal amount, boolean reverse) {

        if ("CREDIT".equalsIgnoreCase(transactionType)) {
            return reverse ? amount.negate() : amount;
        }

        if ("DEBIT".equalsIgnoreCase(transactionType)) {
            return reverse ? amount : amount.negate();
        }

        throw new BadRequestException(transactionType + " is not allowed");
    }

}



