package dev.vishal.expensemanager.controller;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.expensemanager.dto.AccountDto;
import dev.vishal.expensemanager.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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

        if (Objects.isNull(dto.getUserId())) {
            throw new BadRequestException("User Id is mandatory!");
        }

        if (!StringUtils.hasText(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (!StringUtils.hasText(dto.getType())) {
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

        if (Objects.isNull(dto.getUserId())) {
            throw new BadRequestException("User Id is mandatory!");
        }

        if (!StringUtils.hasText(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (!StringUtils.hasText(dto.getType())) {
            throw new BadRequestException("Type is mandatory!");
        }

        return ResponseUtil.sendResponse(accountService.updateAccount(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAccount/{userId}/{id}")
    public ResponseEntity<ResponseDTO> getAccount(
            @PathVariable Long id,
            @PathVariable UUID userId) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAccount";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(userId)) {
            throw new BadRequestException("User Id is mandatory!");
        }

        return ResponseUtil.sendResponse(accountService.getAccount(id, userId), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAccountByType/{userId}/{type}")
    public ResponseEntity<ResponseDTO> getAccount(
            @PathVariable String type,
            @PathVariable UUID userId) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAccountByType";

        if (Objects.isNull(userId)) {
            throw new BadRequestException("User Id is mandatory!");
        }

        if (!StringUtils.hasText(type)) {
            throw new BadRequestException("Type is mandatory!");
        }

        return ResponseUtil.sendResponse(
                accountService.getAccountByType(userId, type), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAllAccounts/{userId}")
    public ResponseEntity<ResponseDTO> getAllAccounts(@PathVariable UUID userId) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAllAccounts";

        return ResponseUtil.sendResponse(accountService.getAllAccounts(userId), landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteAccount/{userId}/{id}")
    public ResponseEntity<ResponseDTO> deleteAccount(
            @PathVariable Long id,
            @PathVariable UUID userId) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/deleteAccount";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (Objects.isNull(userId)) {
            throw new BadRequestException("User Id is mandatory!");
        }

        accountService.deleteAccount(id, userId);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
