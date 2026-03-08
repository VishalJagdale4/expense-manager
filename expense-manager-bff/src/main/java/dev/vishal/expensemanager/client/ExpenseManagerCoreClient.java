package dev.vishal.expensemanager.client;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.expensemanager.dto.AccountDto;
import dev.vishal.expensemanager.dto.CategoryDto;
import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "expense-manager-core",
        url = "${expense.manager.core.url}")
public interface ExpenseManagerCoreClient {

    @PostMapping("/account/createAccount")
    ResponseEntity<ResponseDTO> createAccount(@RequestBody AccountDto dto) throws Exception;

    @PutMapping("/account/updateAccount")
    ResponseEntity<ResponseDTO> updateAccount(@RequestBody AccountDto dto) throws Exception;

    @GetMapping("/account/getAccount/{userId}/{id}")
    ResponseEntity<ResponseDTO> getAccount(
            @PathVariable UUID userId,
            @PathVariable Long id) throws BadRequestException;

    @GetMapping("/account/getAccountByType/{userId}/{type}")
    ResponseEntity<ResponseDTO> getAccountByType(
            @PathVariable UUID userId,
            @PathVariable String type) throws BadRequestException;

    @GetMapping("/account/getAllAccounts/{userId}")
    ResponseEntity<ResponseDTO> getAllAccounts(@PathVariable UUID userId);

    @DeleteMapping("/account/deleteAccount/{userId}/{id}")
    void deleteAccount(@PathVariable Long id, @PathVariable UUID userId) throws BadRequestException;

    @PostMapping("/category/createCategory")
    ResponseEntity<ResponseDTO> createCategory(@RequestBody CategoryDto dto) throws BadRequestException;

    @PutMapping("/category/updateCategory")
    ResponseEntity<ResponseDTO> updateCategory(@RequestBody CategoryDto dto) throws BadRequestException;

    @GetMapping("/category/getCategory/{userId}/{id}")
    ResponseEntity<ResponseDTO> getCategory(
            @PathVariable UUID userId,
            @PathVariable Long id) throws BadRequestException;

    @GetMapping("/category/getCategoryByParent/{userId}/{id}")
    ResponseEntity<ResponseDTO> getCategoryByParent(
            @PathVariable UUID userId,
            @PathVariable Long id) throws BadRequestException;

    @GetMapping("/category/getAllCategories/{userId}")
    ResponseEntity<ResponseDTO> getAllCategories(@PathVariable UUID userId);

    @DeleteMapping("/category/deleteCategory/{userId}/{id}")
    void deleteCategory(@PathVariable UUID userId, @PathVariable Long id) throws BadRequestException;

    @PostMapping("/transaction/createTransaction")
    ResponseEntity<ResponseDTO> createTransaction(@RequestBody TransactionDto dto);

    @PutMapping("/transaction/updateTransaction")
    ResponseEntity<ResponseDTO> updateTransaction(@RequestBody TransactionDto dto) throws BadRequestException;

    @GetMapping("/transaction/getTransaction/{userId}/{id}")
    ResponseEntity<ResponseDTO> getTransaction(
            @PathVariable UUID userId,
            @PathVariable UUID id) throws BadRequestException;

    @GetMapping("/transaction/getAllTransactions")
    ResponseEntity<ResponseDTO> getAllTransactions(@RequestBody TransactionDto dto);

    @GetMapping("/transaction/getTransactionNotes")
    ResponseEntity<ResponseDTO> getTransactionNotes(TransactionDto dto);

    @DeleteMapping("/transaction/deleteTransaction/{userId}/{id}")
    void deleteTransaction(
            @PathVariable UUID userId,
            @PathVariable UUID id) throws BadRequestException;

}
