package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CustomException {
    private static final HttpStatus code = HttpStatus.FORBIDDEN;
    private static final String message = "Action forbidden";
    protected ForbiddenException( String field, String error) {
        super(code, message, field, error);
    }
}
