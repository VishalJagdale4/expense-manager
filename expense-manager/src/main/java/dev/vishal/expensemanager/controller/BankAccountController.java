package dev.vishal.expensemanager.controller;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.common.utils.ResponseDTO;
import dev.vishal.expensemanager.common.utils.ResponseUtil;
import dev.vishal.expensemanager.dto.BankAccountDto;
import dev.vishal.expensemanager.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/bankAccount")
@RequiredArgsConstructor
@Validated
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("/createBankAccount")
    public ResponseEntity<ResponseDTO> createBankAccount(@RequestBody BankAccountDto dto) throws Exception {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/createBankAccount";

        if (Objects.isNull(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (Objects.isNull(dto.getType())) {
            throw new BadRequestException("Type is mandatory!");
        }

        return ResponseUtil.sendResponse(bankAccountService.createBankAccount(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updateBankAccount")
    public ResponseEntity<ResponseDTO> updateBankAccount(@RequestBody BankAccountDto dto) throws Exception {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateBankAccount";

        if (Objects.isNull(dto.getId())) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (Objects.isNull(dto.getType())) {
            throw new BadRequestException("Type is mandatory!");
        }

        return ResponseUtil.sendResponse(bankAccountService.updateBankAccount(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getBankAccount/{id}")
    public ResponseEntity<ResponseDTO> getBankAccount(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getBankAccount";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        return ResponseUtil.sendResponse(bankAccountService.getBankAccount(id), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getBankAccountByType/{type}")
    public ResponseEntity<ResponseDTO> getBankAccount(@PathVariable String type) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getBankAccountByType";

        if (Objects.isNull(type)) {
            throw new BadRequestException("Type is mandatory!");
        }

        return ResponseUtil.sendResponse(bankAccountService.getBankAccountByType(type), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAllBankAccounts")
    public ResponseEntity<ResponseDTO> getAllBankAccounts() {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getBankAccount";

        return ResponseUtil.sendResponse(bankAccountService.getAllBankAccounts(), landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteBankAccount/{id}")
    public ResponseEntity<ResponseDTO> deleteBankAccount(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getBankAccount";

        bankAccountService.deleteBankAccount(id);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
