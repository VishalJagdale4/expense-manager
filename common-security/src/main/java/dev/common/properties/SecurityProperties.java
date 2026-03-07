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

    private List<String> permitAll = new ArrayList<>();
    private final String secret = "ThisIsSpringSecuritySecret256Bit";

    @PostConstruct
    public void mergeDefaults() {
        List<String> defaultUrls = List.of(
                "/auth/login",
                "/auth/refresh",
                "/users/createUser"
        );

        Set<String> merged = new LinkedHashSet<>(defaultUrls);
        if (permitAll != null) {
            merged.addAll(permitAll);
        }
        permitAll = new ArrayList<>(merged);
    }
}