package com.venuehub.commons.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;


public class ConstraintViolationExceptionSerializer implements CustomExcpetion {
    private final int status = HttpStatus.BAD_REQUEST.value();
    private final HttpStatus error = HttpStatus.BAD_REQUEST;
    private final String message;


    public ConstraintViolationExceptionSerializer(ConstraintViolationException e) {
        this.message = e.getConstraintViolations().stream().toString();
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
