package com.venuehub.broker.event.booking;

import com.venuehub.broker.constants.BookingStatus;

import java.awt.print.Book;
import java.io.Serializable;

@BookingCreated
public record BookingCreatedEvent(Long bookingId, Long venueId, BookingStatus status, String username) {
}
