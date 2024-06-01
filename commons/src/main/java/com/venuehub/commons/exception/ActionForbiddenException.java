package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ActionForbiddenException extends RuntimeException implements CustomExcpetion {
    private final HttpStatusCode status = HttpStatus.FORBIDDEN;
    private final HttpStatus error = HttpStatus.FORBIDDEN;
    private final String message;

    public ActionForbiddenException(String msg) {
        super(msg);
        this.message = msg;
    }

    public ActionForbiddenException() {
        this("User is forbidden to perform this action");
    }

    public ErrorResponse getResponse(){
        return new ErrorResponse(status,error, message);
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
