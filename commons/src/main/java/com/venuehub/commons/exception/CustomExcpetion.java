package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public interface CustomExcpetion {
    public ErrorResponse getResponse();
    public HttpStatusCode getCode();
    public HttpStatus getStatus();
}
