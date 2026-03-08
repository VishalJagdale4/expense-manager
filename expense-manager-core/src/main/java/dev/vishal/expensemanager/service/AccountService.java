package dev.vishal.expensemanager.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dto.AccountDto;
import dev.vishal.expensemanager.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    Account createAccount(AccountDto dto) throws BadRequestException;

    Account updateAccount(AccountDto dto) throws BadRequestException;

    Account getAccount(Long id, UUID userId) throws BadRequestException;

    List<Account> getAccountByType(UUID userId, String type) throws BadRequestException;

    List<Account> getAllAccounts(UUID userId);

    void deleteAccount(Long id, UUID userId) throws BadRequestException;
}
