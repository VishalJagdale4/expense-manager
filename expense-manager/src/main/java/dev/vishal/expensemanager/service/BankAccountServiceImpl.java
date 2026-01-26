package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.dto.BankAccountDto;
import dev.vishal.expensemanager.entity.BankAccount;
import dev.vishal.expensemanager.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccount createBankAccount(BankAccountDto dto) throws BadRequestException {

        List<BankAccount> existing =
                bankAccountRepository.findByNameAndTypeAndDeletedFalse(
                        dto.getName(),
                        dto.getType()
                );

        if (!CollectionUtils.isEmpty(existing)) {
            throw new BadRequestException("Bank account already exists!");
        }

        BankAccount account = new BankAccount();
        dto.setBalance(BigDecimal.ZERO);    // Zero Balance at creation
        copyDtoToEntity(dto, account);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount getBankAccount(Long id) throws BadRequestException {
        return bankAccountRepository.findById(id)
                .filter(a -> !a.getDeleted())
                .orElseThrow(() -> new BadRequestException("Bank account not found"));
    }

    @Override
    public List<BankAccount> getBankAccountByType(String type) throws BadRequestException {
        return bankAccountRepository.findByTypeAndDeletedFalseOrderByName(type);
    }

    @Override
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAllByDeletedFalseOrderByName();
    }

    @Override
    @Transactional
    public void deleteBankAccount(Long id) throws BadRequestException {

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .filter(account -> !account.getDeleted())
                .map(account -> {
                    account.setDeleted(true);
                    return account;
                })
                .orElseThrow(() -> new BadRequestException("Bank account not found"));

        bankAccountRepository.save(bankAccount);
    }

    @Override
    @Transactional
    public BankAccount updateBankAccount(BankAccountDto dto) throws BadRequestException {
        BankAccount existing = bankAccountRepository.findById(dto.getId())
                .filter(bankAccount -> !bankAccount.getDeleted())
                .orElseThrow(() -> new BadRequestException("Bank account not found"));

        List<BankAccount> existingDuplicates =
                bankAccountRepository.findByIdNotAndNameAndTypeAndDeletedFalse(
                        dto.getId(),
                        dto.getName(),
                        dto.getType()
                );

        if (!CollectionUtils.isEmpty(existingDuplicates)) {
            throw new BadRequestException("Bank account already exists!");
        }
        dto.setBalance(existing.getBalance());  // Balance as it is
        copyDtoToEntity(dto, existing);
        return bankAccountRepository.save(existing);
    }

    // ------------------ Helper methods ------------------

    private void copyDtoToEntity(BankAccountDto dto, BankAccount entity) {
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setBalance(dto.getBalance());
    }

}
