package com.venuehub.bookingservice.exception;

import com.venuehub.commons.exception.*;


import jakarta.validation.ConstraintViolationException;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchBookingException(NoSuchBookingException e) {

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
    public ResponseEntity<ErrorResponse> handleBookingNotAvailableException(UserForbiddenException e) {
        return new ResponseEntity<>(e.getResponse(), e.getCode());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleJobExecutionException(JobExecutionException e) {
        return new ResponseEntity<>(e.getResponse(), e.getCode());
    }
//    @org.springframework.web.bind.annotation.ExceptionHandler
//    public ResponseEntity<ErrorResponse> handleSchedulerException(SchedulerException e) {
//        return new ResponseEntity<>(e.getResponse(), e.getCode());
//    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ConstraintViolationExceptionSerializer serializer = new ConstraintViolationExceptionSerializer(e);
        return new ResponseEntity<>(serializer.getResponse(), serializer.getResponse().getCode());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
       GeneralException generalException = new GeneralException("General error",e.getMessage());

        return new ResponseEntity<>(generalException.getResponse(), generalException.getCode());
    }
}
