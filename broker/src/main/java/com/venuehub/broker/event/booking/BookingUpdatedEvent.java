package com.venuehub.broker.event.booking;

import com.venuehub.broker.constants.BookingStatus;

public record BookingUpdatedEvent(Long bookingId, BookingStatus status){}
