package com.venuehub.bookingservice.response;

import com.venuehub.broker.constants.BookingStatus;

public record GetBookingVendorResponse(Long bookingId,String venueName, String customerName, String bookingDateTime, BookingStatus status) {
}

