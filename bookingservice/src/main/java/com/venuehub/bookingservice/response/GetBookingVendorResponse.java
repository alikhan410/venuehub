package com.venuehub.bookingservice.response;

import com.venuehub.broker.constants.BookingStatus;

public record GetBookingVendorResponse(
        Long bookingId,
        BookingStatus status,
        String username,
        String venueName,
        Long venueId,
        String bookingDate,
        String reservationExpiry
) {
}

