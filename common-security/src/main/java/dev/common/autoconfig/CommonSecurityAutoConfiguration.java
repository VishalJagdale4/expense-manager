package dev.common.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(SecurityAutoConfiguration.class)
public class CommonSecurityAutoConfiguration {
}
