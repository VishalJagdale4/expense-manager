package dev.commonlib.exceptionutils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends CommonException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.name());
    }
}
