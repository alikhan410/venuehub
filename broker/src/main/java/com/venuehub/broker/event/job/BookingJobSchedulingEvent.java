package com.venuehub.broker.event.job;

import com.venuehub.broker.constants.BookingStatus;

public record BookingJobSchedulingEvent(
        Long bookingId,
        BookingStatus status,
        String bookingDate,
        String reservationExpiry,
        String username
) {
}