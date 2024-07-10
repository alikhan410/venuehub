package com.venuehub.bookingservice.response;

import com.venuehub.broker.constants.BookingStatus;

public record BookingResponse(
        String bookingDate,
        int guests,
        BookingStatus status,
        String venueName,
        Long venueId
) {
}
