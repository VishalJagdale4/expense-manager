package dev.common.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "common.lib.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CommonLibAutoConfiguration {
}
