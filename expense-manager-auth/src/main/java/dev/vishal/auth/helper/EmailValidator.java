package dev.vishal.auth.helper;

import dev.common.exceptionutils.exceptions.BadRequestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    // Regular expression for validating email
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Email is mandatory!");
        }

        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new BadRequestException("Invalid email address format: " + email);
        }
    }

}