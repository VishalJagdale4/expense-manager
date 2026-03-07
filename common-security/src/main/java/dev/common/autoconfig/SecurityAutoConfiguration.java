package dev.common.autoconfig;

import dev.common.filter.JwtAuthenticationFilter;
import dev.common.properties.SecurityProperties;
import dev.common.service.JwtValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityAutoConfiguration {

    @Value("${jwt.secret}")
    private String secret;

    private final SecurityProperties properties;

    @Bean
    public JwtValidationService jwtValidationService() {
        return new JwtValidationService(secret);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtValidationService jwtValidationService) {

        return new JwtAuthenticationFilter(jwtValidationService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                properties.getPermitAll().toArray(new String[0])
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}