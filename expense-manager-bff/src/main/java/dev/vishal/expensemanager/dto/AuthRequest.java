package dev.vishal.expensemanager.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String refreshToken;
}