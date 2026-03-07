package dev.vishal.auth.controller;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.auth.helper.PasswordValidator;
import dev.vishal.auth.model.UserDto;
import dev.vishal.auth.service.UsersService;
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

        // Validates password else throws BadRequestException
        PasswordValidator.validatePassword(dto.getPassword());

        return ResponseUtil.sendResponse(usersService.createUser(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody UserDto dto) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateUser";

        if (Objects.isNull(dto.getId())) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (!StringUtils.hasText(dto.getFirstName())) {
            throw new BadRequestException("First Name is mandatory!");
        }

        if (!StringUtils.hasText(dto.getLastName())) {
            throw new BadRequestException("Last Name is mandatory!");
        }

        return ResponseUtil.sendResponse(usersService.updateUser(dto), landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<ResponseDTO> updatePassword(@RequestBody UserDto dto) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateUser";

        if (Objects.isNull(dto.getId())) {
            throw new BadRequestException("Id is mandatory!");
        }

        // Validates password else throws BadRequestException
        PasswordValidator.validatePassword(dto.getPassword());

        return ResponseUtil.sendResponse(usersService.updatePassword(dto), landingTime, HttpStatus.OK, endPoint);
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
