package com.venuehub.broker.event.job;

import com.venuehub.broker.constants.BookingStatus;

public record BookingJobCancellingEvent(Long bookingId, BookingStatus status) {
}
