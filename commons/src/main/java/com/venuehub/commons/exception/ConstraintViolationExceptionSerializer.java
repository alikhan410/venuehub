package com.venuehub.commons.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ConstraintViolationExceptionSerializer {
    private static final HttpStatus code = HttpStatus.BAD_REQUEST;
    private static final String message = "Invalid parameters";
    ErrorResponse response;

    public ConstraintViolationExceptionSerializer(ConstraintViolationException e) {
        List<ErrorResponse.ErrorDetails> errors = e.getConstraintViolations().stream().map(v -> {

            String fieldName = List.of(v.getPropertyPath().toString().split("\\.")).get(2);

            return new ErrorResponse.ErrorDetails(fieldName, v.getMessage());

        }).toList();

        this.response = new ErrorResponse(code, message, errors);
    }
}
