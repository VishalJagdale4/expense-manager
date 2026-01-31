package dev.commonlib.exceptionutils;

import dev.commonlib.exceptionutils.exceptions.CommonException;
import dev.commonlib.responseutils.model.ResponseDTO;
import dev.commonlib.responseutils.ResponseUtil;
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

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ResponseDTO> handleCommonException(CommonException ex, HttpServletRequest request) {

        if (ex.getStatus().is4xxClientError()) {
            log.warn(
                    "{} | URI: {} | Method: {}",
                    ex.getErrorCode(),
                    request.getRequestURI(),
                    request.getMethod()
            );
        } else {
            log.error(
                    "{} | URI: {} | Method: {}",
                    ex.getErrorCode(),
                    request.getRequestURI(),
                    request.getMethod(),
                    ex
            );
        }

        String errorMessage =
                ex.getStatus().is4xxClientError()
                        ? ex.getMessage()
                        : "Something went wrong, please contact your administrator";

        return ResponseUtil.sendErrorResponse(
                errorMessage,
                ex.getErrorCode(),
                LocalDateTime.now(),
                ex.getStatus(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Internal Server Error | URI: {} | Method: {}",
                request.getRequestURI(),
                request.getMethod(),
                ex

        );

        return ResponseUtil.sendErrorResponse(
                "Something went wrong, please contact your administrator",
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
    }
}

