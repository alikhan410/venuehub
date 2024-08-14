package com.venuehub.broker.event.booking;

import com.venuehub.broker.constants.BookingStatus;

public record BookingCreatedEvent(Long bookingId, Long venueId, BookingStatus status,int bookingFee, String username, String vendorName) {
}
