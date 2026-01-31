package dev.commonlib.responseutils;

import dev.commonlib.responseutils.model.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public final class ResponseUtil {

    private ResponseUtil() {
    } // private constructor to prevent instantiation

    public static ResponseEntity<ResponseDTO> sendResponse(Object data, LocalDateTime localDateTime, HttpStatus httpStatus, String endpoint) {
        ResponseDTO response = ResponseDTO.builder()
                .landingTime(localDateTime)
                .responseTime(LocalDateTime.now())
                .data(data)
                .status(httpStatus.name())
                .statusCode(httpStatus.value())
                .endpoint(endpoint)
                .build();
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<ResponseDTO> sendErrorResponse(String errorMessage, String errorCode, LocalDateTime localDateTime, HttpStatus httpStatus, String endpoint) {
        ResponseDTO response = ResponseDTO.builder()
                .landingTime(localDateTime)
                .responseTime(LocalDateTime.now())
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .status(httpStatus.name())
                .statusCode(httpStatus.value())
                .endpoint(endpoint)
                .build();
        return new ResponseEntity<>(response, httpStatus);
    }

}
