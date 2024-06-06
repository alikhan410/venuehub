package com.venuehub.authservice.exception;

import com.venuehub.commons.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class ExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

//    @org.springframework.web.bind.annotation.ExceptionHandler
//    public ResponseEntity<ErrorResponse> handleUserUnAuthorizedException(UserUnAuthorizedException e) {
//        return new ResponseEntity<>(e.getResponse(), e.getStatus());
//    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchUserException(NoSuchUserException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserForbiddenException(UserForbiddenException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleWrongPasswordException(WrongPasswordException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ValidationSerializer serializer = new ValidationSerializer(e);
        return new ResponseEntity<>(serializer.getResponse(), serializer.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDuplicateEntryException(DuplicateEntryException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationSerializer serializer = new ValidationSerializer(e);
        return new ResponseEntity<>(serializer.getResponse(), serializer.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        LOGGER.info("Encountered a general exception");
        LOGGER.info(e.getMessage());
        GeneralException generalException = new GeneralException(e.getMessage());
        return new ResponseEntity<>(generalException.getResponse(), generalException.getStatus());
    }
}