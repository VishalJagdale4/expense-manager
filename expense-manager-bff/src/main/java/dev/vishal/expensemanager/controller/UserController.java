package dev.vishal.expensemanager.controller;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.expensemanager.client.ExpenseManagerAuthClient;
import dev.vishal.expensemanager.dto.AuthRequest;
import dev.vishal.expensemanager.dto.UserDto;
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
public class UserController {
    private final ExpenseManagerAuthClient authClient;

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

        if (!StringUtils.hasText(dto.getPassword())) {
            throw new BadRequestException("Password is mandatory!");
        }

        Object data = ResponseUtil.getDataFromResponse(authClient.createUser(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody AuthRequest dto) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/login";

        if (!StringUtils.hasText(dto.getUsername())) {
            throw new BadRequestException("Email is mandatory!");
        }

        if (!StringUtils.hasText(dto.getPassword())) {
            throw new BadRequestException("Password is mandatory!");
        }

        Object data = ResponseUtil.getDataFromResponse(authClient.login(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
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

        Object data = ResponseUtil.getDataFromResponse(authClient.updateUser(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<ResponseDTO> updatePassword(@RequestBody UserDto dto) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/updateUser";

        if (Objects.isNull(dto.getId())) {
            throw new BadRequestException("Id is mandatory!");
        }

        if (!StringUtils.hasText(dto.getPassword())) {
            throw new BadRequestException("Password is mandatory!");
        }

        Object data = ResponseUtil.getDataFromResponse(authClient.updatePassword(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<ResponseDTO> getUser(@PathVariable UUID id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/getUser";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        Object data = ResponseUtil.getDataFromResponse(authClient.getUser(id));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }


    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO> refresh(@RequestBody AuthRequest dto) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/refresh";

        if (Objects.isNull(dto.getRefreshToken())) {
            throw new BadRequestException("Refresh Token is mandatory!");
        }

        Object data = ResponseUtil.getDataFromResponse(authClient.refresh(dto));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);

    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable UUID id) throws BadRequestException {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/deleteUser";

        if (Objects.isNull(id)) {
            throw new BadRequestException("Id is mandatory!");
        }

        Object data = ResponseUtil.getDataFromResponse(authClient.deleteUser(id));
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout() {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/logout";

        Object data = ResponseUtil.getDataFromResponse(authClient.logout());
        return ResponseUtil.sendResponse(data, landingTime, HttpStatus.OK, endPoint);
    }
}
