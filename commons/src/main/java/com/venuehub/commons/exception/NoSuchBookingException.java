package com.venuehub.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;


public class NoSuchBookingException extends RuntimeException implements CustomExcpetion {
    private final HttpStatusCode status = HttpStatus.NOT_FOUND;
    private final HttpStatus error = HttpStatus.NOT_FOUND;

    private final String message;

    public NoSuchBookingException(String message)  {
        super(message);
        this.message = message;
    }
    public NoSuchBookingException(){
        this("No Booking Found");
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
