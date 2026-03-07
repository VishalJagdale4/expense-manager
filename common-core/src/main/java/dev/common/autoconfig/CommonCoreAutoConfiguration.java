package dev.common.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        CommonFeignAutoConfiguration.class,
        CommonExceptionHandlerAutoConfiguration.class,
        CommonLogFilterAutoConfiguration.class
})
public class CommonCoreAutoConfiguration {
}
