package com.venuehub.bookingservice.response;

import com.venuehub.broker.constants.BookingStatus;

public record BookingResponse(String bookingDateTime, int guests, BookingStatus status, String venueName, Long venueId) {
}
