package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.dto.AccountDto;
import dev.vishal.expensemanager.entity.Account;
import dev.vishal.expensemanager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(AccountDto dto) throws BadRequestException {

        List<Account> existing =
                accountRepository.findByNameAndTypeAndIsDeletedFalse(
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
    public Account getAccount(Long id) throws BadRequestException {
        return accountRepository.findById(id)
                .filter(a -> !a.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Account not found"));
    }

    @Override
    public List<Account> getAccountByType(String type) throws BadRequestException {
        return accountRepository.findByTypeAndIsDeletedFalseOrderByName(type);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAllByIsDeletedFalseOrderByName();
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) throws BadRequestException {

        Account account = accountRepository.findById(id)
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
                .filter(Account -> !Account.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Account not found"));

        List<Account> existingDuplicates =
                accountRepository.findByIdNotAndNameAndTypeAndIsDeletedFalse(
                        dto.getId(),
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
        entity.setName(dto.getName());
        entity.setType(dto.getType());
    }

}
