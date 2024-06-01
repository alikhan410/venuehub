package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class DuplicateEntryException extends RuntimeException implements CustomExcpetion{
    private final HttpStatusCode status = HttpStatus.BAD_REQUEST;
    private final HttpStatus error = HttpStatus.BAD_REQUEST;
    private final String message;

    public DuplicateEntryException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public ErrorResponse getResponse() {
        return new ErrorResponse(status, error, message);
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
