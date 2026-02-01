package dev.vishal.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    // Enables @Async annotation for asynchronous audit logging
    // Enables @Scheduled annotation for cleanup tasks
}