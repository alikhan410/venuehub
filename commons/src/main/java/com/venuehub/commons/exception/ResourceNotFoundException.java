package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomException{
    private static final HttpStatus code = HttpStatus.NOT_FOUND;
    private static final String message = "Resource Not Found";
    protected ResourceNotFoundException(String field, String error) {
        super(code, message, field, error);
    }
}
