package com.venuehub.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class JobExecutionException extends RuntimeException implements CustomExcpetion {
    private final HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
    private final HttpStatus error = HttpStatus.INTERNAL_SERVER_ERROR;
    private final String message;

    public JobExecutionException(String message) {
        super(message);
        this.message = message;
    }
    public JobExecutionException(){
        this("Job Execution encountered a problem");
    }

    @Override
    public ErrorResponse getResponse() {
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
