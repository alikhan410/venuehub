package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class WrongPasswordException extends RuntimeException implements CustomExcpetion {
    private final HttpStatusCode status = HttpStatus.BAD_REQUEST;
    private final HttpStatus error = HttpStatus.BAD_REQUEST;
    private final String message;

    public WrongPasswordException(String message) {
        super(message);
        this.message = message;
    }

    public WrongPasswordException() {
        this("Password in wrong");
    }

    @Override
    public ErrorResponse getResponse() {
        return new ErrorResponse(status,error, message);
    }

    @Override
    public HttpStatusCode getCode() {
        return status;
    }

    @Override
    public HttpStatus getStatus() {
        return error;
    }
}
