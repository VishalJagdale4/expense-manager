package dev.common.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "common.lib.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@ComponentScan(basePackages = {
        "dev.common.logger",
        "dev.common.exceptionutils",
        "dev.common.responseutils",
        "dev.common.feign"
})
public class CommonLibAutoConfiguration {
}
