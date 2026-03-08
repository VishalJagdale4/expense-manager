package dev.vishal.expensemanager.controller;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.helper.SecurityUtils;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.expensemanager.client.ExpenseManagerCoreClient;
import dev.vishal.expensemanager.dto.AccountDto;
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

    private final ExpenseManagerCoreClient expenseManagerCoreClient;

    @PostMapping("/createAccount")
    public ResponseEntity<ResponseDTO> createAccount(@RequestBody AccountDto dto) throws Exception {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/createAccount";

        if (!StringUtils.hasText(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (!StringUtils.hasText(dto.getType())) {
            throw new BadRequestException("Type is mandatory!");
        }

        // Fetching user id from current user (Security context)
        dto.setUserId(SecurityUtils.getCurrentUser().getUserId());

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.createAccount(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updateAccount")
    public ResponseEntity<ResponseDTO> updateAccount(@RequestBody AccountDto dto) throws Exception {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateAccount";

        if (Objects.isNull(dto.getId())) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (!StringUtils.hasText(dto.getName())) {
            throw new BadRequestException("Name is mandatory!");
        }

        if (!StringUtils.hasText(dto.getType())) {
            throw new BadRequestException("Type is mandatory!");
        }

        // Fetching user id from current user (Security context)
        dto.setUserId(SecurityUtils.getCurrentUser().getUserId());

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.updateAccount(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAccount/{id}")
    public ResponseEntity<ResponseDTO> getAccount(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAccount";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        // Fetching user id from current user (Security context)
        UUID userId = SecurityUtils.getCurrentUser().getUserId();

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.getAccount(userId, id));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAccountByType/{type}")
    public ResponseEntity<ResponseDTO> getAccount(@PathVariable String type) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAccountByType";

        if (!StringUtils.hasText(type)) {
            throw new BadRequestException("Type is mandatory!");
        }

        // Fetching user id from current user (Security context)
        UUID userId = SecurityUtils.getCurrentUser().getUserId();

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.getAccountByType(userId, type));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getAllAccounts")
    public ResponseEntity<ResponseDTO> getAllAccounts() {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getAllAccounts";

        // Fetching user id from current user (Security context)
        UUID userId = SecurityUtils.getCurrentUser().getUserId();

        Object data = ResponseUtil.getDataFromResponse(expenseManagerCoreClient.getAllAccounts(userId));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<ResponseDTO> deleteAccount(@PathVariable Long id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/deleteAccount";

        // Fetching user id from current user (Security context)
        UUID userId = SecurityUtils.getCurrentUser().getUserId();

        expenseManagerCoreClient.deleteAccount(id, userId);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
