package dev.common.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private List<String> permitAllUri = new ArrayList<>();
    private List<String> permitAllEndpoints = new ArrayList<>();
    private final String secret = "ThisIsSpringSecuritySecret256Bit";

    @PostConstruct
    public void mergeDefaults() {
        List<String> defaultUris = List.of(
                "/expense-manager-bff/users/login",
                "/expense-manager-bff/users/refresh",
                "/expense-manager-bff/users/createUser",
                "/expense-manager-auth/auth/login",
                "/expense-manager-auth/auth/refresh",
                "/expense-manager-auth/users/createUser"
        );

        List<String> defaultEndpoints = List.of(
                "/users/login",
                "/users/refresh",
                "/users/createUser",
                "/auth/login",
                "/auth/refresh",
                "/users/createUser"
        );

        Set<String> mergedUris = new LinkedHashSet<>(defaultUris);
        Set<String> mergedEndpoints = new LinkedHashSet<>(defaultEndpoints);

        if (permitAllUri != null) {
            mergedUris.addAll(permitAllUri);
        }

        if (permitAllEndpoints != null) {
            mergedEndpoints.addAll(permitAllEndpoints);
        }

        permitAllUri = new ArrayList<>(mergedUris);
        permitAllEndpoints = new ArrayList<>(mergedEndpoints);
    }
}