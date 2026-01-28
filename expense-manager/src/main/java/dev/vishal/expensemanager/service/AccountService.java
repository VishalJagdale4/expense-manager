package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.dto.AccountDto;
import dev.vishal.expensemanager.entity.Account;

import java.util.List;

public interface AccountService {

    Account createAccount(AccountDto dto) throws BadRequestException;

    Account updateAccount(AccountDto dto) throws BadRequestException;

    Account getAccount(Long id) throws BadRequestException;

    List<Account> getAccountByType(String type) throws BadRequestException;

    List<Account> getAllAccounts();

    void deleteAccount(Long id) throws BadRequestException;
}
