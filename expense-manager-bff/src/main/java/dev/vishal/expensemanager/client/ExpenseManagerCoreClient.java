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

    @GetMapping("/account/getAccount/{id}")
    ResponseEntity<ResponseDTO> getAccount(@PathVariable Long id) throws BadRequestException;

    @GetMapping("/account/getAccountByType/{type}")
    ResponseEntity<ResponseDTO> getAccountByType(@PathVariable String type) throws BadRequestException;

    @GetMapping("/account/getAllAccounts")
    ResponseEntity<ResponseDTO> getAllAccounts();

    @DeleteMapping("/account/deleteAccount/{id}")
    void deleteAccount(@PathVariable Long id) throws BadRequestException;

    @PostMapping("/category/createCategory")
    ResponseEntity<ResponseDTO> createCategory(@RequestBody CategoryDto dto) throws BadRequestException;

    @PutMapping("/category/updateCategory")
    ResponseEntity<ResponseDTO> updateCategory(@RequestBody CategoryDto dto) throws BadRequestException;

    @GetMapping("/category/getCategory/{id}")
    ResponseEntity<ResponseDTO> getCategory(@PathVariable Long id) throws BadRequestException;

    @GetMapping("/category/getCategoryByParent/{id}")
    ResponseEntity<ResponseDTO> getCategoryByParent(@PathVariable Long id) throws BadRequestException;

    @GetMapping("/category/getAllCategories")
    ResponseEntity<ResponseDTO> getAllCategories();

    @DeleteMapping("/category/deleteCategory/{id}")
    void deleteCategory(@PathVariable Long id) throws BadRequestException;

    @PostMapping("/transaction/createTransaction")
    ResponseEntity<ResponseDTO> createTransaction(@RequestBody TransactionDto dto);

    @PutMapping("/transaction/updateTransaction")
    ResponseEntity<ResponseDTO> updateTransaction(@RequestBody TransactionDto dto) throws BadRequestException;

    @GetMapping("/transaction/getTransaction/{id}")
    ResponseEntity<ResponseDTO> getTransaction(@PathVariable UUID id) throws BadRequestException;

    @GetMapping("/transaction/getAllTransactions")
    ResponseEntity<ResponseDTO> getAllTransactions(@PathVariable TransactionDto dto);

    @GetMapping("/transaction/getTransactionNotes")
    List<String> getTransactionNotes(TransactionDto dto);

    @DeleteMapping("/transaction/deleteTransaction/{id}")
    void deleteTransaction(@PathVariable UUID id) throws BadRequestException;

    @PostMapping("/users/createUser")
    ResponseEntity<ResponseDTO> createUser(@RequestBody UserDto dto);

    @PutMapping("/users/updateUser")
    ResponseEntity<ResponseDTO> updateUser(@RequestBody UserDto dto);

    @GetMapping("/users/getUser/{id}")
    ResponseEntity<ResponseDTO> getUser(@PathVariable UUID id) throws BadRequestException;

    @DeleteMapping("/users/deleteUser/{id}")
    void deleteUser(@PathVariable UUID id) throws BadRequestException;

}
