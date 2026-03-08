package dev.common.helper;

import dev.common.model.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static AuthenticatedUser getCurrentUser() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        return (AuthenticatedUser) auth.getPrincipal();
    }
}