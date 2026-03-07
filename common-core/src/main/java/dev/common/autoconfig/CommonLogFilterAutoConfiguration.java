package dev.common.autoconfig;

import dev.common.logger.RequestResponseLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonLogFilterAutoConfiguration {

    @Bean
    public RequestResponseLoggingFilter getRequestResponseLoggingFilter() {
        return new RequestResponseLoggingFilter();
    }

}

