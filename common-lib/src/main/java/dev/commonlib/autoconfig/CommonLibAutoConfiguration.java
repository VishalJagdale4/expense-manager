package dev.commonlib.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "commonlib.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@ComponentScan(basePackages = {
        "dev.commonlib.exceptionutils",
        "dev.commonlib.logger"
})
public class CommonLibAutoConfiguration {
}
