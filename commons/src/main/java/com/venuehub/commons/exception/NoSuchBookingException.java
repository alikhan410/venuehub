package com.venuehub.commons.exception;


import org.springframework.http.HttpStatus;

public class NoSuchBookingException extends RuntimeException implements CustomExcpetion {
    private final int status = HttpStatus.NOT_FOUND.value();
    private final HttpStatus error = HttpStatus.NOT_FOUND;

    private final String message;

    public NoSuchBookingException(String message) {
        super(message);
        this.message = message;
    }

    public NoSuchBookingException() {
        this("No Booking Found");
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
