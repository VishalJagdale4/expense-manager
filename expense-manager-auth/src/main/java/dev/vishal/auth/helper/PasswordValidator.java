package dev.vishal.auth.helper;

import dev.common.exceptionutils.exceptions.BadRequestException;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final Pattern UPPERCASE =
            Pattern.compile("[A-Z]");

    private static final Pattern LOWERCASE =
            Pattern.compile("[a-z]");

    private static final Pattern DIGIT =
            Pattern.compile("[0-9]");

    private static final Pattern SPECIAL =
            Pattern.compile("[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]");

    public static void validatePassword(String password) {

        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password cannot be empty");
        }

        if (password.length() < 8) {
            throw new BadRequestException(
                    "Password must be at least 8 characters long");
        }

        if (!UPPERCASE.matcher(password).find()) {
            throw new BadRequestException(
                    "Password must contain at least one uppercase letter");
        }

        if (!LOWERCASE.matcher(password).find()) {
            throw new BadRequestException(
                    "Password must contain at least one lowercase letter");
        }

        if (!DIGIT.matcher(password).find()) {
            throw new BadRequestException(
                    "Password must contain at least one digit");
        }

        if (!SPECIAL.matcher(password).find()) {
            throw new BadRequestException(
                    "Password must contain at least one special character");
        }
    }
}