package com.venuehub.paymentservice.exception;

import com.venuehub.commons.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        GeneralException generalException = new GeneralException("General error",e.getMessage());

        return new ResponseEntity<>(generalException.getResponse(), generalException.getCode());
    }
}
