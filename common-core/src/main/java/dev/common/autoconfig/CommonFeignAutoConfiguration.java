package dev.common.autoconfig;

import dev.common.exceptionutils.exceptions.InternalServerException;
import dev.common.feign.CustomFeignErrorDecoder;
import dev.common.feign.FeignDefaultsProperties;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;

@AutoConfiguration

@ConditionalOnClass(FeignClient.class)
@EnableConfigurationProperties(FeignDefaultsProperties.class)
public class CommonFeignAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CommonFeignAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public ErrorDecoder feignErrorDecoder() {
        return new CustomFeignErrorDecoder();
    }


    @Bean
    public Retryer retryer() {
        // Custom retryer: retry 2 times with 1 second interval
        return new CustomRetryer(1000, 2000, 2);
    }

    /**
     * Custom Retryer that converts final retry failures to our custom exception
     */
    public static class CustomRetryer implements Retryer {
        private final long period;
        private final long maxPeriod;
        private final int maxAttempts;
        private int attempt = 1;

        public CustomRetryer(long period, long maxPeriod, int maxAttempts) {
            this.period = period;
            this.maxPeriod = maxPeriod;
            this.maxAttempts = maxAttempts;
        }

        @Override
        public void continueOrPropagate(RetryableException e) {
            if (attempt++ >= maxAttempts) {
                log.error("Feign client max retries exceeded. Service may be down: {}", e.getMessage());

                // Convert to our custom exception instead of throwing RetryableException
                throw new InternalServerException("Unable to connect to downstream service. Please try again later.");
            }

            long interval = nextMaxInterval();
            log.warn("Feign retry attempt {} of {} after {}ms delay", attempt, maxAttempts, interval);

            try {
                Thread.sleep(interval);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                throw new InternalServerException("Service connection interrupted");
            }
        }

        private long nextMaxInterval() {
            long interval = (long) (period * Math.pow(1.5, attempt - 1));
            return Math.min(interval, maxPeriod);
        }

        @Override
        public Retryer clone() {
            return new CustomRetryer(period, maxPeriod, maxAttempts);
        }
    }
}

