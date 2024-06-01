package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class NotAuthenticatedException extends RuntimeException implements CustomExcpetion{

    private final HttpStatusCode status = HttpStatus.UNAUTHORIZED;
    private final HttpStatus error = HttpStatus.UNAUTHORIZED;
    private final String message;

    public NotAuthenticatedException(String message) {
        super(message);
        this.message = message;
    }
    public NotAuthenticatedException(){
        this("Unable to authenticate, please log in again");
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
