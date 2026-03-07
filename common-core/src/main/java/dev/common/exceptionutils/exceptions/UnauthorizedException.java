package dev.common.exceptionutils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends CommonException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.name());
    }

    public UnauthorizedException(String message, String errorCode) {
        super(message, HttpStatus.UNAUTHORIZED, errorCode);
    }

}
