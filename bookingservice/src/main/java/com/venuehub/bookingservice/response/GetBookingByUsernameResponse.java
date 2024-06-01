package com.venuehub.bookingservice.response;

import com.venuehub.broker.constants.BookingStatus;

import java.time.LocalDateTime;

public record GetBookingByUsernameResponse(Long id, BookingStatus status, String username, String venueName,
                                           String bookingDate) {
}
