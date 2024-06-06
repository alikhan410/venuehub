package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;

public class ActionForbiddenException extends RuntimeException implements CustomExcpetion {
    private final int status = HttpStatus.FORBIDDEN.value();
    private final HttpStatus error = HttpStatus.FORBIDDEN;
    private final String message;

    public ActionForbiddenException(String msg) {
        super(msg);
        this.message = msg;
    }

    public ActionForbiddenException() {
        this("User is forbidden to perform this action");
    }

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
