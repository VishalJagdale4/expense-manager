package dev.vishal.expensemanager.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dto.AccountDto;
import dev.vishal.expensemanager.entity.Account;
import dev.vishal.expensemanager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

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
        return accountRepository.findById(id)
                .filter(a -> a.getUserId().equals(userId))
                .filter(a -> !a.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Account not found"));
    }

    @Override
    public List<Account> getAccountByType(UUID userId, String type) throws BadRequestException {
        return accountRepository.findByUserIdAndTypeAndIsDeletedFalseOrderByName(userId, type);
    }

    @Override
    public List<Account> getAllAccounts(UUID userId) {
        return accountRepository.findAllByUserIdAndIsDeletedFalseOrderByName(userId);
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

}
