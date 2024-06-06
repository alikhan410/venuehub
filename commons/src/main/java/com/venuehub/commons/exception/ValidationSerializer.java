package com.venuehub.commons.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationSerializer implements CustomExcpetion {
    private final int status = HttpStatus.BAD_REQUEST.value();
    private final HttpStatus error = HttpStatus.BAD_REQUEST;
    private final String message;

    public ValidationSerializer(ConstraintViolationException e) {
        this.message = e.getConstraintViolations().stream().toString();
    }

    public ValidationSerializer(MethodArgumentNotValidException e) {

        List<String> messages = new ArrayList<>();
        String regex = "default message \\[(.*?)\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(e.getMessage());
        while (matcher.find()) {
            messages.add(matcher.group(1));
        }

        this.message = messages.get(1);

    }

    @Override
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
