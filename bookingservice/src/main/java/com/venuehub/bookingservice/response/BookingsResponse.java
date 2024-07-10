package com.venuehub.bookingservice.response;

import com.venuehub.bookingservice.dto.BookingDto;

import java.util.List;

public record BookingsResponse(List<BookingDto> bookings) {
}
