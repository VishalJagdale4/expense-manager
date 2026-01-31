package dev.commonlib.exceptionutils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CommonException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    protected CommonException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

}
