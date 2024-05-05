package com.venuehub.broker.event.job;

import com.venuehub.broker.constants.BookingStatus;

public record BookingJobSchedulingEvent(Long bookingId, BookingStatus status,String bookingDateTime, String username) {
}