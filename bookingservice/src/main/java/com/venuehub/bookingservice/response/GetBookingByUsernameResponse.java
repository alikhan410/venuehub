package com.venuehub.bookingservice.response;

import com.venuehub.broker.constants.BookingStatus;

import java.time.LocalDateTime;

public record GetBookingByUsernameResponse(
        Long bookingId,
        BookingStatus status,
        String username,
        String venueName,
        Long venueId,
        String bookingDate,
        String reservationExpiry
) {
}

