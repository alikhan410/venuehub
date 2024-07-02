package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderStatusException extends RuntimeException implements CustomExcpetion {
    private final int status = HttpStatus.BAD_REQUEST.value();
    private final HttpStatus error = HttpStatus.BAD_REQUEST;

    private final String message;

    public InvalidOrderStatusException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidOrderStatusException() {
        this("Invalid Order Status");
    }

    @Override
    public ErrorResponse getResponse() {
        return new ErrorResponse(status, error, message);
    }

    @Override
    public int getCode() {
        return status;
    }

    @Override
    public HttpStatus getStatus() {
        return error;
    }
}
