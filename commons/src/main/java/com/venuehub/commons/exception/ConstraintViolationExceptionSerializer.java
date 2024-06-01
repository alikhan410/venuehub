package com.venuehub.commons.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public class ConstraintViolationExceptionSerializer implements CustomExcpetion {
    private final HttpStatusCode status = HttpStatus.BAD_REQUEST;
    private final HttpStatus error = HttpStatus.BAD_REQUEST;
    private final String message;


    public ConstraintViolationExceptionSerializer(ConstraintViolationException e) {
        this.message = e.getConstraintViolations().stream().toString();
    }

    @Override
    public ErrorResponse getResponse() {
        return null;
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
