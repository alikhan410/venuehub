package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends CustomException{
    private static final HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final String message = "Encountered an unexpected error on server";
    protected InternalServerException( String field, String error) {
        super(code, message, field, error);
    }
}
