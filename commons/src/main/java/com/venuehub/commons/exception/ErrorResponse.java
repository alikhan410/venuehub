package com.venuehub.commons.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class ErrorResponse {

    private final HttpStatusCode status;
    private final HttpStatus error;
    private final String message;

    public ErrorResponse(HttpStatusCode status, HttpStatus error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

}

