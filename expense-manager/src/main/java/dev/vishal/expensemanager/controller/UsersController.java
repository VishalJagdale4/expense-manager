package dev.vishal.expensemanager.controller;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.common.utils.ResponseDTO;
import dev.vishal.expensemanager.common.utils.ResponseUtil;
import dev.vishal.expensemanager.dto.UserDto;
import dev.vishal.expensemanager.service.UsersService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/createUser")
    public ResponseEntity<ResponseDTO> createUser(@RequestBody UserDto dto) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/createUser";

        if (!StringUtils.hasText(dto.getFirstName())) {
            throw new BadRequestException("First Name is mandatory!");
        }

        if (!StringUtils.hasText(dto.getLastName())) {
            throw new BadRequestException("Last Name is mandatory!");
        }

        if (!StringUtils.hasText(dto.getEmail())) {
            throw new BadRequestException("Email is mandatory!");
        }

        return ResponseUtil.sendResponse(usersService.createUser(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody UserDto dto) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateUser";

        if (Objects.isNull(dto.getFirstName())) {
            throw new BadRequestException("First Name is mandatory!");
        }

        if (Objects.isNull(dto.getLastName())) {
            throw new BadRequestException("Last Name is mandatory!");
        }

        return ResponseUtil.sendResponse(usersService.updateUser(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<ResponseDTO> getUser(@PathVariable UUID id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getUser";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        return ResponseUtil.sendResponse(usersService.getUser(id), landingTime, HttpStatus.OK, endPoint);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable UUID id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/deleteUser";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        usersService.deleteUser(id);
        return ResponseUtil.sendResponse(id, landingTime, HttpStatus.OK, endPoint);
    }
}
