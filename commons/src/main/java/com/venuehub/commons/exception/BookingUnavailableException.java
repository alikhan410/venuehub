package com.venuehub.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class BookingUnavailableException extends BadRequestException {

    private static final String field="Booking date";
    private static final String error = "No bookings available within this date";

    public BookingUnavailableException() {
        super(field, error);
    }
}