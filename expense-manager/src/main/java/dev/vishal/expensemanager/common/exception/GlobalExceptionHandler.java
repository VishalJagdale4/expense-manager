package dev.vishal.expensemanager.common.exception;

import dev.vishal.expensemanager.common.utils.ResponseDTO;
import dev.vishal.expensemanager.common.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDTO> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {

        log.warn(
                "Bad Request | URI: {} | Method: {}",
                request.getRequestURI(),
                request.getMethod(),
                ex
        );

        return ResponseUtil.sendResponse(
                ex.getMessage(),
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Internal Server Error | URI: {} | Method: {}",
                request.getRequestURI(),
                request.getMethod(),
                ex

        );

        return ResponseUtil.sendResponse(
                "Something went wrong, please contact your administrator",
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
    }
}

