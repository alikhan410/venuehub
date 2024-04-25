package com.venuehub.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class NoSuchBookingException extends ResourceNotFoundException {
    private static final String field="Id";
    private static final String error = "No bookings associated with this id";
    public NoSuchBookingException()  {
        super( field, error);
    }
}
