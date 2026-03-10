package dev.vishal.auth.controller;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.exceptionutils.exceptions.UnauthorizedException;
import dev.common.responseutils.ResponseUtil;
import dev.common.responseutils.model.ResponseDTO;
import dev.vishal.auth.entity.RefreshTokens;
import dev.vishal.auth.entity.Users;
import dev.vishal.auth.model.AuthRequest;
import dev.vishal.auth.model.AuthResponse;
import dev.vishal.auth.model.RefreshRequest;
import dev.vishal.auth.security.JwtService;
import dev.vishal.auth.service.RefreshTokenService;
import dev.vishal.auth.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UsersService usersService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody AuthRequest authRequest) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/login";
        Users user = usersService.getUserByEmail(authRequest.username());

        if (!passwordEncoder.matches(authRequest.password(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.createRefreshToken(user.getId(), refreshToken);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseUtil.sendResponse(authResponse, landingTime, HttpStatus.OK, endPoint);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO> refresh(@RequestBody RefreshRequest request) {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/refresh";

        if (Objects.isNull(request.refreshToken())) {
            throw new UnauthorizedException("Refresh Token is mandatory!");
        }

        RefreshTokens refreshToken =
                refreshTokenService.verifyRefreshToken(request.refreshToken());

        Users user = usersService.getUser(refreshToken.getUserId());

        String newAccessToken = jwtService.generateToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.refreshToken())
                .build();

        return ResponseUtil.sendResponse(authResponse, landingTime, HttpStatus.OK, endPoint);

    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout() {
        LocalDateTime landingTime = LocalDateTime.now();
        String endPoint = "/logout";

        refreshTokenService.deleteByUser();

        return ResponseUtil.sendResponse("Success", landingTime, HttpStatus.OK, endPoint);

    }

}