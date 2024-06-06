package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;

public interface CustomExcpetion {
    public ErrorResponse getResponse();

    public int getCode();

    public HttpStatus getStatus();
}
