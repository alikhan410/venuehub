package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;

public class NotAuthorizedException extends CustomException{
    private static final HttpStatus code = HttpStatus.UNAUTHORIZED;
    private static final String message = "Not Authorized to view this information";
    protected NotAuthorizedException(String field, String error) {
        super(code, message, field, error);
    }
}
