package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.dto.BankAccountDto;
import dev.vishal.expensemanager.entity.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankAccountService {

    BankAccount createBankAccount(BankAccountDto dto) throws BadRequestException;

    BankAccount updateBankAccount(BankAccountDto dto) throws BadRequestException;

    BankAccount getBankAccount(Long id) throws BadRequestException;

    List<BankAccount> getBankAccountByType(String type) throws BadRequestException;

    List<BankAccount> getAllBankAccounts();

    void deleteBankAccount(Long id) throws BadRequestException;
}
