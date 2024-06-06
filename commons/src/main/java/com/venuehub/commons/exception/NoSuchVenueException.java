package com.venuehub.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;


@Getter
public class NoSuchVenueException extends RuntimeException implements CustomExcpetion {
    private final int status = HttpStatus.NOT_FOUND.value();
    private final HttpStatus error = HttpStatus.NOT_FOUND;
    private final String message;

    public NoSuchVenueException(String message) {
        super(message);
        this.message = message;
    }

    public NoSuchVenueException() {
        this("No Venue Found");
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
