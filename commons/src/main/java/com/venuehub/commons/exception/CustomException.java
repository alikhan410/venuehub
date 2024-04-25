package com.venuehub.commons.exception;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
abstract public class CustomException extends RuntimeException{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomException.class);
    private final HttpStatus code;
    private final String message;
    private final String field;
    private final String error;
    private final ErrorResponse response;
    protected CustomException(HttpStatus code, String message, String field, String error) {
        super(message);
        this.code = code;
        this.message = message;
        this.field = field;
        this.error = error;

        ErrorResponse.ErrorDetails details = new ErrorResponse.ErrorDetails(field, error);
        this.response = new ErrorResponse(code, message, List.of(details));
        LOGGER.info(error);
    }

}
