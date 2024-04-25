package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomException{
    private static final HttpStatus code = HttpStatus.BAD_REQUEST;
    private static final String message = "Something went wrong";
    protected BadRequestException( String field, String error) {
        super(code, message, field, error);
    }
}
