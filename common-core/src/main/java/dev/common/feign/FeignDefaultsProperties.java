package dev.common.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "common.feign.defaults")
public class FeignDefaultsProperties {
    private final int connectTimeout = 2000;
    private final int readTimeout = 3000;
}
