package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class NoSuchUserException extends RuntimeException implements CustomExcpetion{
    private final HttpStatusCode status = HttpStatus.NOT_FOUND;
    private final HttpStatus error = HttpStatus.NOT_FOUND;
    private final String message;

    public NoSuchUserException(String message)  {
        super(message);
        this.message = message;
    }
    public NoSuchUserException(){
        this("No User Found");
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
