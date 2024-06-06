package com.venuehub.bookingservice.exception;

import com.venuehub.commons.exception.*;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
public class ExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchBookingException(NoSuchBookingException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoSuchVenueException(NoSuchVenueException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBookingNotAvailableException(BookingUnavailableException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }
        @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBookingNotAvailableException(UserForbiddenException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleJobExecutionException(JobExecutionException e) {
        return new ResponseEntity<>(e.getResponse(), e.getStatus());
    }
//    @org.springframework.web.bind.annotation.ExceptionHandler
//    public ResponseEntity<ErrorResponse> handleSchedulerException(SchedulerException e) {
//        return new ResponseEntity<>(e.getResponse(), e.getStatus());
//    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ConstraintViolationExceptionSerializer serializer = new ConstraintViolationExceptionSerializer(e);
        return new ResponseEntity<>(serializer.getResponse(), serializer.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.info(e.getMessage());
        ValidationSerializer serializer = new ValidationSerializer(e);
        return new ResponseEntity<>(serializer.getResponse(), serializer.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) throws Exception {
        throw e;
//        LOGGER.info("Encountered a general exception");
//        LOGGER.info(e.getMessage());
//        GeneralException generalException = new GeneralException(e.getMessage());
//        return new ResponseEntity<>(generalException.getResponse(), generalException.getStatus());
    }
}
