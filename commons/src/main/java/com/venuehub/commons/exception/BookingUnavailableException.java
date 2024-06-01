package com.venuehub.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;

@Getter
public class BookingUnavailableException extends RuntimeException implements CustomExcpetion {
    private final HttpStatusCode status = HttpStatus.NOT_FOUND;
    private final HttpStatus error = HttpStatus.NOT_FOUND;
    private final String message;

    public BookingUnavailableException(String message) {
        super(message);
        this.message = message;
    }

    public BookingUnavailableException(){
        this("Booking is not available");
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