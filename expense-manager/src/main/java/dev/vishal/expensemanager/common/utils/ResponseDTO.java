package dev.vishal.expensemanager.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
    private LocalDateTime landingTime;
    private LocalDateTime processingTime;
    private Object data;
    private String status;
    private int statusCode;
    private String endpoint;
    private String errorMessage;
    private String exceptionMessage;
}
