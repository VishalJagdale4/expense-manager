package dev.vishal.expensemanager.controller;

import dev.commonlib.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dto.AccountDto;
import dev.vishal.expensemanager.service.AccountService;
import dev.commonlib.responseutils.ResponseUtil;
import dev.commonlib.responseutils.model.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/createAccount")
    public ResponseEntity<ResponseDTO> createAccount(@RequestBody AccountDto dto) throws Exception {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/createAccount";

        if (Objects.isNull(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (Objects.isNull(dto.getType())) {
            throw new BadRequestException("Type is mandatory!");
        }

        return ResponseUtil.sendResponse(accountService.createAccount(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updateAccount")
    public ResponseEntity<ResponseDTO> updateAccount(@RequestBody AccountDto dto) throws Exception {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateAccount";

        if (Objects.isNull(dto.getId())) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (Objects.isNull(dto.getType())) {
            throw new BadRequestException("Type is mandatory!");
        }

        return ResponseUtil.sendResponse(accountService.updateAccount(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAccount/{id}")
    public ResponseEntity<ResponseDTO> getAccount(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAccount";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        return ResponseUtil.sendResponse(accountService.getAccount(id), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAccountByType/{type}")
    public ResponseEntity<ResponseDTO> getAccount(@PathVariable String type) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAccountByType";

        if (Objects.isNull(type)) {
            throw new BadRequestException("Type is mandatory!");
        }

        return ResponseUtil.sendResponse(accountService.getAccountByType(type), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAllAccounts")
    public ResponseEntity<ResponseDTO> getAllAccounts() {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAccount";

        return ResponseUtil.sendResponse(accountService.getAllAccounts(), landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<ResponseDTO> deleteAccount(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAccount";

        accountService.deleteAccount(id);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
