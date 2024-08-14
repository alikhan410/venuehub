package com.venuehub.bookingservice.response;

import com.venuehub.broker.constants.BookingStatus;

public record BookingStatusResponse(
        String bookingDate,
        int guests,
        BookingStatus status,
        String venueName,
        Long venueId
) {
}
