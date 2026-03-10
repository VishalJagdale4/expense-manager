package dev.vishal.expensemanager.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dao.TransactionsDao;
import dev.vishal.expensemanager.dto.AccountDto;
import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;
import dev.vishal.expensemanager.entity.Account;
import dev.vishal.expensemanager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionsDao transactionsDao;

    @Override
    public Account createAccount(AccountDto dto) throws BadRequestException {

        List<Account> existing =
                accountRepository.findByUserIdAndNameAndTypeAndIsDeletedFalse(
                        dto.getUserId(),
                        dto.getName(),
                        dto.getType()
                );

        if (!CollectionUtils.isEmpty(existing)) {
            throw new BadRequestException("Account already exists!");
        }

        Account account = new Account();
        dto.setBalance(BigDecimal.ZERO);    // Zero Balance at creation
        copyDtoToEntity(dto, account);
        return accountRepository.save(account);
    }

    @Override
    public Account getAccount(Long id, UUID userId) throws BadRequestException {
        Account account = accountRepository.findById(id)
                .filter(a -> a.getUserId().equals(userId))
                .filter(a -> !a.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Account not found"));

        // Get All txn against account and calculate balance
        setCalculatedBalance(userId, List.of(account));

        return account;
    }

    @Override
    public List<Account> getAccountByType(UUID userId, String type) throws BadRequestException {
        List<Account> accounts = accountRepository.findByUserIdAndTypeAndIsDeletedFalseOrderByName(userId, type);

        // Get All txn against account and calculate balance
        setCalculatedBalance(userId, accounts);

        return accounts;
    }

    @Override
    public List<Account> getAllAccounts(UUID userId) {
        List<Account> accounts = accountRepository.findAllByUserIdAndIsDeletedFalseOrderByName(userId);

        // Get All txn against account and calculate balance
        setCalculatedBalance(userId, accounts);

        return accounts;
    }

    @Override
    @Transactional
    public void deleteAccount(Long id, UUID userId) throws BadRequestException {

        Account account = accountRepository.findById(id)
                .filter(acc -> acc.getUserId().equals(userId))
                .filter(acc -> !acc.getIsDeleted())
                .map(acc -> {
                    acc.setIsDeleted(true);
                    return acc;
                })
                .orElseThrow(() -> new BadRequestException("Account not found"));

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account updateAccount(AccountDto dto) throws BadRequestException {
        Account existing = accountRepository.findById(dto.getId())
                .filter(Account -> Account.getUserId().equals(dto.getUserId()))
                .filter(Account -> !Account.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Account not found"));

        List<Account> existingDuplicates =
                accountRepository.findByIdNotAndUserIdAndNameAndTypeAndIsDeletedFalse(
                        dto.getId(),
                        dto.getUserId(),
                        dto.getName(),
                        dto.getType()
                );

        if (!CollectionUtils.isEmpty(existingDuplicates)) {
            throw new BadRequestException("Account already exists!");
        }
        copyDtoToEntity(dto, existing);
        return accountRepository.save(existing);
    }

    // ------------------ Helper methods ------------------

    private void copyDtoToEntity(AccountDto dto, Account entity) {
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
    }

    private void setCalculatedBalance(UUID userId, List<Account> accounts) {
        List<Long> accountIds = accounts.stream()
                .map(Account::getId)
                .toList();

        TransactionDto transactionDto = TransactionDto.builder()
                .accounts(accountIds)
                .orderByAsc(Boolean.TRUE)
                .userId(userId)
                .build();

        List<TransactionResponseDto> transactions = transactionsDao.findTransactions(transactionDto);

        Map<Long, List<TransactionResponseDto>> txnAccountMap =
                transactions.stream()
                        .collect(Collectors.groupingBy(TransactionResponseDto::getAccountId));

        accounts.forEach(account -> {

            BigDecimal balance = txnAccountMap
                    .getOrDefault(account.getId(), Collections.emptyList())
                    .stream()
                    .map(TransactionResponseDto::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            account.setBalance(balance);
        });
    }

}
