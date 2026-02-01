package dev.common.responseutils.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
    private LocalDateTime landingTime;
    private LocalDateTime responseTime;
    private String status;
    private int statusCode;
    private String endpoint;

    // Success
    private Object data;

    // Error
    private String errorMessage;
    private String errorCode;
}
