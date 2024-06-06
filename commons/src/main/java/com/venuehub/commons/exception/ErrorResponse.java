package com.venuehub.commons.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private final int status;
    private final HttpStatus error;
    private final String message;

    public ErrorResponse(int status, HttpStatus error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

}

