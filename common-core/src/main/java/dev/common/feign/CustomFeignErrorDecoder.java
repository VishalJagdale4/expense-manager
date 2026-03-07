package dev.common.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.common.exceptionutils.exceptions.ForbiddenException;
import dev.common.exceptionutils.exceptions.InternalServerException;
import dev.common.exceptionutils.exceptions.UnauthorizedException;
import dev.common.responseutils.model.ResponseDTO;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(CustomFeignErrorDecoder.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign client error | Method: {} | Status: {}", methodKey, response.status());

        try {
            // Try to read error response body
            String errorMessage = "Service temporarily unavailable";

            if (response.body() != null) {
                try (InputStream bodyStream = response.body().asInputStream()) {
                    objectMapper.registerModule(new JavaTimeModule());
                    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    ResponseDTO errorResponse = objectMapper.readValue(bodyStream, ResponseDTO.class);
                    if (errorResponse != null) {
                        errorMessage = errorResponse.getErrorMessage() != null
                                ? errorResponse.getErrorMessage()
                                : errorMessage;
                    }
                } catch (IOException e) {
                    log.warn("Failed to parse error response body: {}", e.getMessage());
                }
            }

            // Map HTTP status to custom exceptions
            return switch (response.status()) {
                case 400 -> new BadRequestException(errorMessage);
                case 401 -> new UnauthorizedException(errorMessage);
                case 403 -> new ForbiddenException(errorMessage);
                case 404 -> new BadRequestException(errorMessage);
                case 500, 502, 503, 504 -> new InternalServerException(
                        "Downstream service error: " + errorMessage
                );
                default -> new InternalServerException("Unexpected error from downstream service");
            };

        } catch (Exception e) {
            log.error("Error decoding Feign error response", e);
            return new InternalServerException("Error communicating with downstream service");
        }
    }
}