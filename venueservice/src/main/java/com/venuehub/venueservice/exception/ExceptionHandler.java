package com.venuehub.venueservice.exception;

import com.venuehub.commons.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchBookingException(NoSuchBookingException e) {
        return new ResponseEntity<>(e.getResponse(), e.getCode());
    }
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserUnAuthorizedException(UserUnAuthorizedException e) {
        return new ResponseEntity<>(e.getResponse(), e.getCode());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchVenueException(NoSuchVenueException e) {
        return new ResponseEntity<>(e.getResponse(), e.getCode());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBookingNotAvailableException(BookingUnavailableException e) {
        return new ResponseEntity<>(e.getResponse(), e.getCode());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ConstraintViolationExceptionSerializer serializer = new ConstraintViolationExceptionSerializer(e);
        return new ResponseEntity<>(serializer.getResponse(), serializer.getResponse().getCode());
    }
}