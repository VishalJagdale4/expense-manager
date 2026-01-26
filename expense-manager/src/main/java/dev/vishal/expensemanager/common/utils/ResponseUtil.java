package dev.vishal.expensemanager.common.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseUtil {

    private ResponseUtil() {
    } // private constructor to prevent instantiation

    public static ResponseEntity<ResponseDTO> sendResponse(Object data, LocalDateTime localDateTime, HttpStatus httpStatus, String endpoint) {
        ResponseDTO response = ResponseDTO.builder()
                .landingTime(localDateTime)
                .processingTime(LocalDateTime.now())
                .data(data)
                .status(httpStatus.name())
                .statusCode(httpStatus.value())
                .endpoint(endpoint)
                .build();
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<ResponseDTO> sendResponse(String errorMessage, String exceptionMessage, LocalDateTime localDateTime, HttpStatus httpStatus, String endpoint) {
        ResponseDTO response = ResponseDTO.builder()
                .landingTime(localDateTime)
                .processingTime(LocalDateTime.now())
                .errorMessage(errorMessage)
                .exceptionMessage(exceptionMessage)
                .status(httpStatus.name())
                .statusCode(httpStatus.value())
                .endpoint(endpoint)
                .build();
        return new ResponseEntity<>(response, httpStatus);
    }

}
