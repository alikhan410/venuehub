package com.venuehub.paymentservice.exception;

import com.venuehub.commons.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchBookingException(NoSuchBookingException e) {

        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchVenueException(NoSuchVenueException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchVenueException(UserForbiddenException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ConstraintViolationExceptionSerializer serializer = new ConstraintViolationExceptionSerializer(e);
        return new ResponseEntity<>(serializer.getResponse(), serializer.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) throws Exception {
        throw e;
//        GeneralException generalException = new GeneralException(e.getMessage());
//        return new ResponseEntity<>(generalException.getResponse(), generalException.getStatus());
    }

}
