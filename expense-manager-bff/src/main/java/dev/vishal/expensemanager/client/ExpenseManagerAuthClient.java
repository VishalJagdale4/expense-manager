package dev.vishal.expensemanager.client;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.expensemanager.dto.AuthRequest;
import dev.vishal.expensemanager.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "expense-manager-auth",
        url = "${expense.manager.auth.url}")
public interface ExpenseManagerAuthClient {

    @PostMapping("/users/createUser")
    ResponseEntity<ResponseDTO> createUser(@RequestBody UserDto dto) throws BadRequestException;

    @PostMapping("/auth/login")
    ResponseEntity<ResponseDTO> login(@RequestBody AuthRequest dto);

    @PutMapping("/users/updateUser")
    ResponseEntity<ResponseDTO> updateUser(@RequestBody UserDto dto) throws BadRequestException;

    @PutMapping("/users/updatePassword")
    ResponseEntity<ResponseDTO> updatePassword(@RequestBody UserDto dto) throws BadRequestException;

    @GetMapping("/users/getUser/{id}")
    ResponseEntity<ResponseDTO> getUser(@PathVariable UUID id) throws BadRequestException;

    @PostMapping("/auth/refresh")
    ResponseEntity<ResponseDTO> refresh(@RequestBody AuthRequest dto);

    @DeleteMapping("users/deleteUser/{id}")
    ResponseEntity<ResponseDTO> deleteUser(@PathVariable UUID id) throws BadRequestException;

    @PostMapping("/auth/logout")
    ResponseEntity<ResponseDTO> logout();
}
